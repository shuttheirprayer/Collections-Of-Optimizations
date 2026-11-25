package com.misanthropy.collections_of_optimizations.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.service.MixinService;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

final class MixinFitCheck {

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    private static final String COERCE = "Lorg/spongepowered/asm/mixin/injection/Coerce;";
    private static final String WRAP_METHOD = "Lcom/llamalad7/mixinextras/injector/wrapmethod/WrapMethod;";
    private static final String MODIFY_RETURN_VALUE = "Lcom/llamalad7/mixinextras/injector/ModifyReturnValue;";
    private static final Type CALLBACK_INFO = Type.getObjectType("org/spongepowered/asm/mixin/injection/callback/CallbackInfo");
    private static final Type CALLBACK_INFO_RETURNABLE = Type.getObjectType("org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable");
    private static final Type OPERATION = Type.getObjectType("com/llamalad7/mixinextras/injector/wrapoperation/Operation");
    private static final Set<String> OBJECT_METHODS = Set.of("equals", "hashCode", "toString", "getClass", "notify", "notifyAll", "wait", "clone", "finalize");
    private static final String[] ACCESSOR_PREFIXES = {"get", "set", "is", "call", "invoke", "new", "create"};
    private static final ClassNode MISSING = new ClassNode();

    private final Map<String, ClassNode> classes = new HashMap<>();
    private final Map<String, Map<String, String>> refmap = new HashMap<>();

    MixinFitCheck(String refmapResource) {
        try (InputStream in = MixinFitCheck.class.getClassLoader().getResourceAsStream(refmapResource)) {
            if (in == null) {
                return;
            }
            JsonObject mappings = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("mappings");
            if (mappings == null) {
                return;
            }
            for (Map.Entry<String, JsonElement> mixin : mappings.entrySet()) {
                Map<String, String> entries = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : mixin.getValue().getAsJsonObject().entrySet()) {
                    entries.put(entry.getKey(), entry.getValue().getAsString());
                }
                refmap.put(mixin.getKey(), entries);
            }
        } catch (Throwable ignored) {
        }
    }

    String mismatch(String mixinClassName) {
        ClassNode mixin = read(mixinClassName.replace('.', '/'));
        if (mixin == MISSING) {
            return null;
        }
        AnnotationNode mixinAnnotation = annotation(mixin.visibleAnnotations, mixin.invisibleAnnotations, MIXIN);
        if (mixinAnnotation == null) {
            return null;
        }
        boolean defaultRemap = !Boolean.FALSE.equals(value(mixinAnnotation, "remap"));
        List<String> targets = new ArrayList<>();
        for (Object type : list(value(mixinAnnotation, "value"))) {
            targets.add(((Type) type).getInternalName());
        }
        for (Object name : list(value(mixinAnnotation, "targets"))) {
            targets.add(((String) name).replace('.', '/'));
        }
        Map<String, String> mappings = refmap.getOrDefault(mixin.name, Map.of());
        for (String targetName : targets) {
            if (isGameClass(targetName)) {
                continue;
            }
            ClassNode target = read(targetName);
            if (target == MISSING) {
                continue;
            }
            String problem = checkMembers(mixin, target, mappings, defaultRemap);
            if (problem == null) {
                problem = checkReferences(mixin, modPrefix(targetName));
            }
            if (problem != null) {
                return problem;
            }
        }
        return null;
    }

    private String checkMembers(ClassNode mixin, ClassNode target, Map<String, String> mappings, boolean defaultRemap) {
        for (FieldNode field : mixin.fields) {
            AnnotationNode shadow = annotation(field.visibleAnnotations, field.invisibleAnnotations, SHADOW);
            if (shadow != null && !hasField(target, shadowNames(shadow, field.name), field.desc)) {
                return "field " + field.name + " is gone from " + target.name;
            }
        }
        for (MethodNode method : mixin.methods) {
            AnnotationNode shadow = annotation(method.visibleAnnotations, method.invisibleAnnotations, SHADOW);
            if (shadow != null && !hasMethod(target, shadowNames(shadow, method.name), method.desc)) {
                return "method " + method.name + method.desc + " is gone from " + target.name;
            }
            if (annotation(method.visibleAnnotations, method.invisibleAnnotations, OVERWRITE) != null
                    && !hasMethod(target, List.of(method.name), method.desc)) {
                return "method " + method.name + method.desc + " is gone from " + target.name;
            }
            AnnotationNode accessor = annotation(method.visibleAnnotations, method.invisibleAnnotations, ACCESSOR);
            if (accessor != null && !hasField(target, accessorNames(accessor, method.name, mappings, defaultRemap), null)) {
                return "field behind accessor " + method.name + " is gone from " + target.name;
            }
            AnnotationNode invoker = annotation(method.visibleAnnotations, method.invisibleAnnotations, INVOKER);
            if (invoker != null && !invokesConstructor(invoker, method.name) && !hasMethod(target, accessorNames(invoker, method.name, mappings, defaultRemap), null)) {
                return "method behind invoker " + method.name + " is gone from " + target.name;
            }
            for (AnnotationNode injector : annotations(method)) {
                Object selectors = value(injector, "method");
                if (selectors == null) {
                    continue;
                }
                boolean remap = remap(injector, defaultRemap);
                Object require = value(injector, "require");
                boolean found = false;
                for (Object selector : list(selectors)) {
                    String resolved = mapped((String) selector, mappings, remap);
                    if (resolved.contains("*") || resolved.startsWith("^")) {
                        found = true;
                        continue;
                    }
                    List<MethodNode> candidates = targetMethods(target, resolved);
                    for (MethodNode candidate : candidates) {
                        String problem = checkHandler(injector.desc, method, candidate);
                        if (problem != null) {
                            return problem + " (" + method.name + " on " + target.name + "." + candidate.name + candidate.desc + ")";
                        }
                    }
                    found |= !candidates.isEmpty();
                }
                if (!found && require instanceof Integer && (Integer) require > 0) {
                    return "required target of " + method.name + " is gone from " + target.name;
                }
            }
        }
        return null;
    }

    private static List<MethodNode> targetMethods(ClassNode target, String selector) {
        int ownerEnd = selector.indexOf(';');
        int descStart = selector.indexOf('(');
        if (selector.startsWith("L") && ownerEnd > 0 && (descStart < 0 || ownerEnd < descStart)) {
            selector = selector.substring(ownerEnd + 1);
            descStart = selector.indexOf('(');
        }
        String name = descStart < 0 ? selector : selector.substring(0, descStart);
        String desc = descStart < 0 ? null : selector.substring(descStart);
        List<MethodNode> matches = new ArrayList<>();
        for (MethodNode candidate : target.methods) {
            if (candidate.name.equals(name) && (desc == null || candidate.desc.equals(desc))) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    private static String checkHandler(String kind, MethodNode handler, MethodNode target) {
        boolean handlerStatic = (handler.access & Opcodes.ACC_STATIC) != 0;
        boolean targetStatic = (target.access & Opcodes.ACC_STATIC) != 0;
        Type[] handlerArgs = Type.getArgumentTypes(handler.desc);
        Type[] targetArgs = Type.getArgumentTypes(target.desc);
        Type targetReturn = Type.getReturnType(target.desc);
        switch (kind) {
            case INJECT -> {
                if (handlerStatic != targetStatic) {
                    return "static modifier no longer matches";
                }
                if (handler.invisibleParameterAnnotations != null && hasCoerce(handler)) {
                    return null;
                }
                Type info = targetReturn.getSort() == Type.VOID ? CALLBACK_INFO : CALLBACK_INFO_RETURNABLE;
                if (handlerArgs.length == 1 && handlerArgs[0].equals(info)) {
                    return null;
                }
                if (handlerArgs.length <= targetArgs.length || !handlerArgs[targetArgs.length].equals(info)) {
                    return "arguments no longer match";
                }
                for (int i = 0; i < targetArgs.length; i++) {
                    if (!handlerArgs[i].equals(targetArgs[i])) {
                        return "arguments no longer match";
                    }
                }
                return null;
            }
            case WRAP_METHOD -> {
                if (handlerStatic != targetStatic) {
                    return "static modifier no longer matches";
                }
                if (handlerArgs.length != targetArgs.length + 1 || !handlerArgs[targetArgs.length].equals(OPERATION)
                        || !Type.getReturnType(handler.desc).equals(targetReturn)) {
                    return "signature no longer matches";
                }
                for (int i = 0; i < targetArgs.length; i++) {
                    if (!handlerArgs[i].equals(targetArgs[i])) {
                        return "signature no longer matches";
                    }
                }
                return null;
            }
            case MODIFY_RETURN_VALUE -> {
                if (!Type.getReturnType(handler.desc).equals(targetReturn)) {
                    return "return type no longer matches";
                }
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    private String checkReferences(ClassNode mixin, String prefix) {
        if (prefix == null) {
            return null;
        }
        for (MethodNode method : mixin.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                String problem = null;
                if (insn instanceof MethodInsnNode call) {
                    problem = checkMethodReference(prefix, call.owner, call.name, call.desc);
                } else if (insn instanceof FieldInsnNode field) {
                    problem = checkFieldReference(prefix, field.owner, field.name, field.desc);
                } else if (insn instanceof TypeInsnNode type) {
                    problem = checkClassReference(prefix, type.desc);
                } else if (insn instanceof InvokeDynamicInsnNode dynamic) {
                    for (Object argument : dynamic.bsmArgs) {
                        if (argument instanceof Handle handle) {
                            problem = handle.getTag() <= Opcodes.H_PUTSTATIC
                                    ? checkFieldReference(prefix, handle.getOwner(), handle.getName(), handle.getDesc())
                                    : checkMethodReference(prefix, handle.getOwner(), handle.getName(), handle.getDesc());
                            if (problem != null) {
                                break;
                            }
                        }
                    }
                }
                if (problem != null) {
                    return problem + " (used by " + method.name + ")";
                }
            }
        }
        return null;
    }

    private String checkClassReference(String prefix, String owner) {
        if (owner.startsWith("[") || !owner.startsWith(prefix)) {
            return null;
        }
        return read(owner) == MISSING ? "class " + owner + " no longer exists" : null;
    }

    private String checkMethodReference(String prefix, String owner, String name, String desc) {
        if (owner.startsWith("[") || !owner.startsWith(prefix)) {
            return null;
        }
        if (read(owner) == MISSING) {
            return "class " + owner + " no longer exists";
        }
        return memberReachable(owner, prefix, name, desc, true, new HashSet<>()) ? null : "method " + owner + "." + name + desc + " no longer exists";
    }

    private String checkFieldReference(String prefix, String owner, String name, String desc) {
        if (owner.startsWith("[") || !owner.startsWith(prefix)) {
            return null;
        }
        if (read(owner) == MISSING) {
            return "class " + owner + " no longer exists";
        }
        return memberReachable(owner, prefix, name, desc, false, new HashSet<>()) ? null : "field " + owner + "." + name + " no longer exists";
    }

    private boolean memberReachable(String owner, String prefix, String name, String desc, boolean method, Set<String> visited) {
        if (owner == null || !visited.add(owner)) {
            return false;
        }
        if (owner.equals("java/lang/Object")) {
            return method && OBJECT_METHODS.contains(name);
        }
        if (!owner.startsWith(prefix)) {
            return true;
        }
        ClassNode node = read(owner);
        if (node == MISSING) {
            return true;
        }
        if (method ? hasMethod(node, List.of(name), desc) : hasField(node, List.of(name), desc)) {
            return true;
        }
        if (memberReachable(node.superName, prefix, name, desc, method, visited)) {
            return true;
        }
        for (String face : node.interfaces) {
            if (memberReachable(face, prefix, name, desc, method, visited)) {
                return true;
            }
        }
        return false;
    }

    private synchronized ClassNode read(String internalName) {
        ClassNode cached = classes.get(internalName);
        if (cached != null) {
            return cached;
        }
        ClassNode node;
        try {
            node = MixinService.getService().getBytecodeProvider().getClassNode(internalName);
        } catch (Throwable throwable) {
            node = null;
        }
        if (node == null) {
            node = MISSING;
        }
        classes.put(internalName, node);
        return node;
    }

    private static boolean isGameClass(String internalName) {
        return internalName.startsWith("net/minecraft/") || internalName.startsWith("com/mojang/") || internalName.startsWith("java/");
    }

    private static String modPrefix(String internalName) {
        String[] parts = internalName.split("/");
        if (parts.length < 3) {
            return null;
        }
        int depth = Math.min(3, parts.length - 1);
        return String.join("/", Arrays.copyOf(parts, depth)) + "/";
    }

    private static boolean hasField(ClassNode node, List<String> names, String desc) {
        for (FieldNode field : node.fields) {
            if (names.contains(field.name) && (desc == null || desc.equals(field.desc))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasMethod(ClassNode node, List<String> names, String desc) {
        for (MethodNode method : node.methods) {
            if (names.contains(method.name) && (desc == null || desc.equals(method.desc))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> shadowNames(AnnotationNode shadow, String memberName) {
        Object prefixValue = value(shadow, "prefix");
        String prefix = prefixValue instanceof String s ? s : "shadow$";
        List<String> names = new ArrayList<>();
        names.add(memberName.startsWith(prefix) ? memberName.substring(prefix.length()) : memberName);
        for (Object alias : list(value(shadow, "aliases"))) {
            names.add((String) alias);
        }
        return names;
    }

    private static List<String> accessorNames(AnnotationNode accessor, String methodName, Map<String, String> mappings, boolean defaultRemap) {
        Object explicit = value(accessor, "value");
        List<String> names = new ArrayList<>();
        if (explicit instanceof String s && !s.isEmpty()) {
            names.add(s);
        } else {
            for (String prefix : ACCESSOR_PREFIXES) {
                if (methodName.length() > prefix.length() && methodName.startsWith(prefix)
                        && Character.isUpperCase(methodName.charAt(prefix.length()))) {
                    String rest = methodName.substring(prefix.length());
                    names.add(rest);
                    names.add(Character.toLowerCase(rest.charAt(0)) + rest.substring(1));
                    break;
                }
            }
        }
        if (remap(accessor, defaultRemap)) {
            for (String name : List.copyOf(names)) {
                String mapped = mappings.get(name);
                if (mapped != null) {
                    int colon = mapped.indexOf(':');
                    int paren = mapped.indexOf('(');
                    int end = colon >= 0 ? colon : paren >= 0 ? paren : mapped.length();
                    int start = mapped.indexOf(';') >= 0 && mapped.indexOf(';') < end ? mapped.indexOf(';') + 1 : 0;
                    names.add(mapped.substring(start, end));
                }
            }
        }
        return names;
    }

    private static boolean invokesConstructor(AnnotationNode invoker, String methodName) {
        Object explicit = value(invoker, "value");
        if (explicit instanceof String s && !s.isEmpty()) {
            return s.equals("<init>");
        }
        return methodName.startsWith("new") || methodName.startsWith("create");
    }

    private static String mapped(String selector, Map<String, String> mappings, boolean remap) {
        if (!remap) {
            return selector;
        }
        String mapped = mappings.get(selector);
        return mapped != null ? mapped : selector;
    }

    private static boolean remap(AnnotationNode annotation, boolean defaultRemap) {
        Object remap = value(annotation, "remap");
        return remap instanceof Boolean b ? b : defaultRemap;
    }

    private static boolean hasCoerce(MethodNode handler) {
        for (List<AnnotationNode> parameter : handler.invisibleParameterAnnotations) {
            if (parameter != null && annotation(parameter, null, COERCE) != null) {
                return true;
            }
        }
        return false;
    }

    private static List<AnnotationNode> annotations(MethodNode method) {
        List<AnnotationNode> all = new ArrayList<>();
        if (method.visibleAnnotations != null) {
            all.addAll(method.visibleAnnotations);
        }
        if (method.invisibleAnnotations != null) {
            all.addAll(method.invisibleAnnotations);
        }
        return all;
    }

    private static AnnotationNode annotation(List<AnnotationNode> visible, List<AnnotationNode> invisible, String desc) {
        for (List<AnnotationNode> list : Arrays.asList(visible, invisible)) {
            if (list == null) {
                continue;
            }
            for (AnnotationNode annotation : list) {
                if (desc.equals(annotation.desc)) {
                    return annotation;
                }
            }
        }
        return null;
    }

    private static Object value(AnnotationNode annotation, String key) {
        if (annotation.values == null) {
            return null;
        }
        for (int i = 0; i + 1 < annotation.values.size(); i += 2) {
            if (key.equals(annotation.values.get(i))) {
                return annotation.values.get(i + 1);
            }
        }
        return null;
    }

    private static List<?> list(Object value) {
        if (value == null) {
            return List.of();
        }
        return value instanceof List<?> l ? l : List.of(value);
    }
}
