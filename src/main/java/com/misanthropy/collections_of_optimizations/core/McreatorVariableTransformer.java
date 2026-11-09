package com.misanthropy.collections_of_optimizations.core;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;

public final class McreatorVariableTransformer implements ILaunchPluginService {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final EnumSet<Phase> BEFORE = EnumSet.of(Phase.BEFORE);
    private static final EnumSet<Phase> NEVER = EnumSet.noneOf(Phase.class);

    private static final String DEFAULTS_OWNER =
            "com/misanthropy/collections_of_optimizations/core/McreatorVariableDefaults";
    private static final String DEFAULTS_METHOD = "shared";
    private static final String DEFAULTS_DESCRIPTOR = "(I)Ljava/lang/Object;";

    private static final String LAZY_OPTIONAL = "net/minecraftforge/common/util/LazyOptional";
    private static final String ORELSE_DESCRIPTOR = "(Ljava/lang/Object;)Ljava/lang/Object;";

    private static final Unsafe UNSAFE = unsafe();

    public static void install() {
        McreatorVariableDefaults.scan();
        try {
            Object handler = readField(Launcher.INSTANCE, "launchPlugins");
            @SuppressWarnings("unchecked")
            Map<String, ILaunchPluginService> plugins =
                    (Map<String, ILaunchPluginService>) readField(handler, "plugins");
            McreatorVariableTransformer transformer = new McreatorVariableTransformer();
            plugins.put(transformer.name(), transformer);
        } catch (Throwable throwable) {
            LOGGER.warn("Could not install the MCreator player variable transformer, "
                    + "those allocations stay as they are.", throwable);
        }
    }

    @Override
    public String name() {
        return "collections_of_optimizations_mcreator";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return isEmpty || find(classType.getClassName()) == null ? NEVER : BEFORE;
    }

    @Override
    public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
        McreatorVariableDefaults.Entry entry = find(classType.getClassName());
        if (entry == null) {
            return ComputeFlags.NO_REWRITE;
        }
        boolean rewritten = false;
        for (MethodNode method : classNode.methods) {
            rewritten |= rewrite(method, entry);
        }
        return rewritten ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

    private static McreatorVariableDefaults.Entry find(String className) {
        for (McreatorVariableDefaults.Entry entry : McreatorVariableDefaults.entries()) {
            if (className.startsWith(entry.packagePrefix)) {
                return entry;
            }
        }
        return null;
    }

    private static boolean rewrite(MethodNode method, McreatorVariableDefaults.Entry entry) {
        boolean rewritten = false;
        AbstractInsnNode insn = method.instructions.getFirst();
        while (insn != null) {
            AbstractInsnNode next = insn.getNext();
            if (insn.getOpcode() == Opcodes.NEW && entry.internalName.equals(((TypeInsnNode) insn).desc)) {
                AbstractInsnNode dup = nextInstruction(insn);
                AbstractInsnNode constructor = nextInstruction(dup);
                AbstractInsnNode orElse = nextInstruction(constructor);
                if (isAllocation(dup, constructor, entry) && isOrElse(orElse)) {
                    InsnList replacement = new InsnList();
                    replacement.add(intConstant(entry.index));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DEFAULTS_OWNER, DEFAULTS_METHOD,
                            DEFAULTS_DESCRIPTOR, false));
                    method.instructions.insertBefore(insn, replacement);
                    method.instructions.remove(insn);
                    method.instructions.remove(dup);
                    method.instructions.remove(constructor);
                    rewritten = true;
                    next = orElse;
                }
            }
            insn = next;
        }
        return rewritten;
    }

    private static AbstractInsnNode nextInstruction(AbstractInsnNode from) {
        AbstractInsnNode next = from == null ? null : from.getNext();
        while (next != null
                && (next.getType() == AbstractInsnNode.LABEL || next.getType() == AbstractInsnNode.LINE)) {
            next = next.getNext();
        }
        return next;
    }

    private static boolean isAllocation(AbstractInsnNode dup, AbstractInsnNode constructor,
                                        McreatorVariableDefaults.Entry entry) {
        return dup != null
                && dup.getOpcode() == Opcodes.DUP
                && constructor instanceof MethodInsnNode call
                && call.getOpcode() == Opcodes.INVOKESPECIAL
                && call.name.equals("<init>")
                && call.desc.equals("()V")
                && entry.internalName.equals(call.owner);
    }

    private static boolean isOrElse(AbstractInsnNode insn) {
        return insn instanceof MethodInsnNode call
                && call.getOpcode() == Opcodes.INVOKEVIRTUAL
                && LAZY_OPTIONAL.equals(call.owner)
                && call.name.equals("orElse")
                && call.desc.equals(ORELSE_DESCRIPTOR);
    }

    private static AbstractInsnNode intConstant(int value) {
        if (value <= 5) {
            return new InsnNode(Opcodes.ICONST_0 + value);
        }
        if (value <= Byte.MAX_VALUE) {
            return new IntInsnNode(Opcodes.BIPUSH, value);
        }
        return new IntInsnNode(Opcodes.SIPUSH, value);
    }

    private static Object readField(Object owner, String name) throws NoSuchFieldException {
        Field field = owner.getClass().getDeclaredField(name);
        return UNSAFE.getObject(owner, UNSAFE.objectFieldOffset(field));
    }

    private static Unsafe unsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
