package com.misanthropy.collections_of_optimizations.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class ListenerGuards {

    public static final int STAGE = 0;
    public static final int PHASE = 1;
    public static final int SIDE = 2;
    public static final int ENTITY = 3;
    public static final int LEVEL = 4;
    public static final int CLIENT_SIDE = 5;
    public static final int ENTITY_LEVEL = 6;

    public static final int EQUALS = 0;
    public static final int NOT_EQUALS = 1;
    public static final int IS_INSTANCE = 2;
    public static final int NOT_INSTANCE = 3;
    public static final int IS_TRUE = 4;
    public static final int IS_FALSE = 5;

    private static final String MIXIN_MERGED = "Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;";
    private static final String LEVEL_DESC = "Lnet/minecraft/world/level/Level;";

    private ListenerGuards() {
    }

    public record Guard(int value, int test, String owner, String name) {
    }

    public static List<Guard> analyze(Method method) {
        try {
            Class<?> owner = method.getDeclaringClass();
            ClassLoader loader = owner.getClassLoader();
            if (loader == null) {
                return null;
            }
            String internal = owner.getName().replace('.', '/');
            byte[] bytes;
            try (InputStream in = loader.getResourceAsStream(internal + ".class")) {
                if (in == null) {
                    return null;
                }
                bytes = in.readAllBytes();
            }
            ClassNode node = new ClassNode();
            new ClassReader(bytes).accept(node, 0);
            if (!internal.equals(node.name) || patched(owner, method.getName(), loader)) {
                return null;
            }
            String desc = Type.getMethodDescriptor(method);
            for (MethodNode m : node.methods) {
                if (m.name.equals(method.getName()) && m.desc.equals(desc)) {
                    return guards(m, Modifier.isStatic(method.getModifiers()) ? 0 : 1);
                }
            }
            return null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static boolean patched(Class<?> owner, String listener, ClassLoader loader) {
        try {
            java.util.Set<String> mixins = new java.util.HashSet<>();
            for (Method m : owner.getDeclaredMethods()) {
                for (java.lang.annotation.Annotation a : m.getDeclaredAnnotations()) {
                    if (Type.getDescriptor(a.annotationType()).equals(MIXIN_MERGED)) {
                        mixins.add((String) a.annotationType().getMethod("mixin").invoke(a));
                    }
                }
            }
            for (String mixin : mixins) {
                if (touches(mixin, listener, loader)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable t) {
            return true;
        }
    }

    private static boolean touches(String mixin, String listener, ClassLoader loader) throws java.io.IOException {
        byte[] bytes;
        try (InputStream in = loader.getResourceAsStream(mixin.replace('.', '/') + ".class")) {
            if (in == null) {
                return true;
            }
            bytes = in.readAllBytes();
        }
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, ClassReader.SKIP_CODE);
        for (MethodNode m : node.methods) {
            List<org.objectweb.asm.tree.AnnotationNode> all = new ArrayList<>();
            if (m.visibleAnnotations != null) all.addAll(m.visibleAnnotations);
            if (m.invisibleAnnotations != null) all.addAll(m.invisibleAnnotations);
            for (org.objectweb.asm.tree.AnnotationNode a : all) {
                if (a.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;") && m.name.equals(listener)) {
                    return true;
                }
                if (a.values == null) {
                    continue;
                }
                for (int i = 0; i + 1 < a.values.size(); i += 2) {
                    if (!"method".equals(a.values.get(i))) {
                        continue;
                    }
                    Object value = a.values.get(i + 1);
                    List<?> targets = value instanceof List<?> list ? list : List.of(value);
                    for (Object target : targets) {
                        if (!(target instanceof String text)) {
                            return true;
                        }
                        String name = text;
                        int paren = name.indexOf('(');
                        if (paren >= 0) name = name.substring(0, paren);
                        int dot = name.lastIndexOf(';');
                        if (dot >= 0) name = name.substring(dot + 1);
                        if (name.isEmpty() || name.contains("*") || name.startsWith("/") || name.equals(listener)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static List<Guard> guards(MethodNode m, int slot) {
        if (!m.desc.endsWith(")V")) {
            return null;
        }
        List<Guard> out = new ArrayList<>();
        java.util.Map<Integer, Integer> aliases = new java.util.HashMap<>();
        AbstractInsnNode at = real(m.instructions.getFirst());
        while (at != null) {
            if (!(at instanceof VarInsnNode load) || load.getOpcode() != Opcodes.ALOAD) {
                break;
            }
            AbstractInsnNode cursor = real(at.getNext());
            int value = -1;
            if (load.var != slot) {
                Integer aliased = aliases.get(load.var);
                if (aliased == null) {
                    break;
                }
                value = aliased;
            } else if (cursor instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKEVIRTUAL && call.desc.startsWith("()")) {
                if (call.name.equals("getStage") && call.desc.equals("()Lnet/minecraftforge/client/event/RenderLevelStageEvent$Stage;")) {
                    value = STAGE;
                } else if (call.name.equals("getEntity") && call.desc.startsWith("()Lnet/minecraft/world/entity/")) {
                    value = ENTITY;
                }
            } else if (cursor instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETFIELD) {
                if (field.name.equals("phase") && field.desc.equals("Lnet/minecraftforge/event/TickEvent$Phase;")) {
                    value = PHASE;
                } else if (field.name.equals("side") && field.desc.equals("Lnet/minecraftforge/fml/LogicalSide;")) {
                    value = SIDE;
                } else if (field.name.equals("player") && field.desc.equals("Lnet/minecraft/world/entity/player/Player;")) {
                    value = ENTITY;
                } else if (field.name.equals("level") && field.desc.equals(LEVEL_DESC)) {
                    value = LEVEL;
                }
            }
            if (value < 0) {
                break;
            }
            if (load.var == slot) {
                cursor = real(cursor.getNext());
            }
            if (value == ENTITY && cursor instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && call.desc.equals("()" + LEVEL_DESC) && (call.name.equals("m_9236_") || call.name.equals("level"))) {
                value = ENTITY_LEVEL;
                cursor = real(cursor.getNext());
            }
            boolean flip = false;
            if (value == LEVEL || value == ENTITY_LEVEL) {
                if (cursor instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETFIELD && field.desc.equals("Z")
                        && (field.name.equals("f_46443_") || field.name.equals("isClientSide"))) {
                    value = CLIENT_SIDE;
                    cursor = real(cursor.getNext());
                } else if (cursor instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKEVIRTUAL && call.desc.equals("()Z")
                        && (call.name.equals("m_5776_") || call.name.equals("isClientSide"))) {
                    value = CLIENT_SIDE;
                    cursor = real(cursor.getNext());
                }
            } else if (value == SIDE && cursor instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && call.owner.equals("net/minecraftforge/fml/LogicalSide") && call.desc.equals("()Z")) {
                if (call.name.equals("isClient")) {
                    value = CLIENT_SIDE;
                } else if (call.name.equals("isServer")) {
                    value = CLIENT_SIDE;
                    flip = true;
                } else {
                    break;
                }
                cursor = real(cursor.getNext());
            }

            if ((value == ENTITY || value == LEVEL || value == ENTITY_LEVEL) && cursor instanceof VarInsnNode store
                    && store.getOpcode() == Opcodes.ASTORE && store.var != slot) {
                AbstractInsnNode after = real(store.getNext());
                boolean pattern = after instanceof VarInsnNode reload && reload.getOpcode() == Opcodes.ALOAD && reload.var == store.var
                        && real(reload.getNext()) instanceof TypeInsnNode check && check.getOpcode() == Opcodes.INSTANCEOF;
                if (!pattern) {
                    aliases.put(store.var, value);
                    at = after;
                    continue;
                }
            }
            String owner = null;
            String name = null;
            boolean pass;
            JumpInsnNode jump;
            if (value == STAGE || value == PHASE || value == SIDE) {
                if (!(cursor instanceof FieldInsnNode constant) || constant.getOpcode() != Opcodes.GETSTATIC) {
                    break;
                }
                owner = constant.owner;
                name = constant.name;
                cursor = real(cursor.getNext());
                if (!(cursor instanceof JumpInsnNode j) || (j.getOpcode() != Opcodes.IF_ACMPEQ && j.getOpcode() != Opcodes.IF_ACMPNE)) {
                    break;
                }
                jump = j;
                pass = j.getOpcode() == Opcodes.IF_ACMPEQ;
            } else if (value == ENTITY || value == LEVEL || value == ENTITY_LEVEL) {
                if (cursor instanceof VarInsnNode store && store.getOpcode() == Opcodes.ASTORE) {
                    AbstractInsnNode reload = real(store.getNext());
                    if (!(reload instanceof VarInsnNode r) || r.getOpcode() != Opcodes.ALOAD || r.var != store.var) {
                        break;
                    }
                    cursor = real(reload.getNext());
                }
                if (!(cursor instanceof TypeInsnNode type) || type.getOpcode() != Opcodes.INSTANCEOF) {
                    break;
                }
                name = type.desc;
                cursor = real(cursor.getNext());
                if (!(cursor instanceof JumpInsnNode j) || (j.getOpcode() != Opcodes.IFEQ && j.getOpcode() != Opcodes.IFNE)) {
                    break;
                }
                jump = j;
                pass = j.getOpcode() == Opcodes.IFNE;
            } else {
                if (!(cursor instanceof JumpInsnNode j) || (j.getOpcode() != Opcodes.IFEQ && j.getOpcode() != Opcodes.IFNE)) {
                    break;
                }
                jump = j;
                pass = j.getOpcode() == Opcodes.IFNE;
            }

            AbstractInsnNode taken = jump.label;
            AbstractInsnNode fall = jump.getNext();
            boolean jumpTrue;
            AbstractInsnNode next;
            if (returns(fall)) {
                jumpTrue = true;
                next = taken;
            } else if (returns(taken)) {
                jumpTrue = false;
                next = fall;
            } else {
                break;
            }
            boolean conditionTrueContinues = jumpTrue == pass;
            int test;
            if (value == STAGE || value == PHASE || value == SIDE) {
                test = conditionTrueContinues ? EQUALS : NOT_EQUALS;
            } else if (value == ENTITY || value == LEVEL || value == ENTITY_LEVEL) {
                test = conditionTrueContinues ? IS_INSTANCE : NOT_INSTANCE;
            } else {
                test = conditionTrueContinues != flip ? IS_TRUE : IS_FALSE;
            }
            out.add(new Guard(value, test, owner, name));
            at = real(next);
        }
        return out.isEmpty() ? null : out;
    }

    private static boolean returns(AbstractInsnNode from) {
        AbstractInsnNode insn = real(from);
        for (int hops = 0; insn != null && hops < 8; hops++) {
            if (insn.getOpcode() == Opcodes.RETURN) {
                return true;
            }
            if (insn.getOpcode() == Opcodes.GOTO) {
                insn = real(((JumpInsnNode) insn).label);
                continue;
            }
            return false;
        }
        return false;
    }

    private static AbstractInsnNode real(AbstractInsnNode insn) {
        while (insn != null && (insn.getOpcode() < 0 || insn instanceof LabelNode)) {
            insn = insn.getNext();
        }
        return insn;
    }
}
