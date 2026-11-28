package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ServerMobWarmup {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final String FACTORY = "net/minecraft/world/entity/EntityType$EntityFactory";
    private static final String MOB = "net/minecraft/world/entity/Mob";
    private static final String LIVING = "net/minecraft/world/entity/LivingEntity";
    private static final int MAX_CLASSES = 20000;

    private static boolean started;

    private ServerMobWarmup() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ServerMobWarmup::onServerStarted);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        if (started || !CoOConfig.vanillaServerMobWarmup) {
            return;
        }
        started = true;
        Set<EntityType<?>> types = nightTypes(event.getServer());
        if (types.isEmpty()) {
            return;
        }
        Object[] factories = factoriesOf(types);
        MinecraftServer server = event.getServer();
        Thread thread = new Thread(() -> new Run(server, factories).run(), "CoO mob class warmup");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private static Set<EntityType<?>> nightTypes(MinecraftServer server) {
        Set<EntityType<?>> types = new LinkedHashSet<>();
        try {
            for (Biome biome : server.registryAccess().registryOrThrow(Registries.BIOME)) {
                for (MobSpawnSettings.SpawnerData data : biome.getMobSettings().getMobs(MobCategory.MONSTER).unwrap()) {
                    types.add(data.type);
                }
            }
        } catch (Throwable t) {
            LOGGER.debug("Mob class warmup could not read the biome spawn lists", t);
            types.clear();
        }
        return types;
    }

    private static Object[] factoriesOf(Set<EntityType<?>> types) {
        Field field = null;
        for (Field candidate : EntityType.class.getDeclaredFields()) {
            if (!Modifier.isStatic(candidate.getModifiers()) && candidate.getType() == EntityType.EntityFactory.class) {
                field = candidate;
                break;
            }
        }
        if (field == null) {
            return new Object[0];
        }
        List<Object> out = new ArrayList<>(types.size());
        try {
            field.setAccessible(true);
            for (EntityType<?> type : types) {
                if (ForgeRegistries.ENTITY_TYPES.getKey(type) == null) {
                    continue;
                }
                Object factory = field.get(type);
                if (factory != null) {
                    out.add(factory);
                }
            }
        } catch (Throwable t) {
            return new Object[0];
        }
        return out.toArray();
    }

    private static final class Run {

        private final MinecraftServer server;
        private final Object[] factories;
        private final ClassLoader loader = EntityType.class.getClassLoader();
        private final Set<String> linked = new HashSet<>();
        private final Set<String> scanned = new HashSet<>();
        private int failed;

        Run(MinecraftServer server, Object[] factories) {
            this.server = server;
            this.factories = factories;
        }

        void run() {
            long begin = System.nanoTime();
            int entities = 0;
            try {
                Set<String> hosts = new LinkedHashSet<>();
                for (Object factory : factories) {
                    String name = factory.getClass().getName();
                    int lambda = name.indexOf("$$Lambda");
                    hosts.add((lambda > 0 ? name.substring(0, lambda) : name).replace('.', '/'));
                }
                Set<String> entityClasses = new LinkedHashSet<>();
                for (String host : hosts) {
                    collectEntityClasses(host, entityClasses);
                }
                for (String entity : entityClasses) {
                    if (!server.isRunning() || linked.size() >= MAX_CLASSES) {
                        break;
                    }
                    if (link(entity)) {
                        entities++;
                        warmGoals(entity);
                    }
                }
            } catch (Throwable t) {
                LOGGER.debug("Mob class warmup stopped early", t);
            }
            LOGGER.info("Mob class warmup linked {} classes ({} entity classes, {} skipped) in {} ms on a background thread",
                    linked.size(), entities, failed, (System.nanoTime() - begin) / 1_000_000L);
        }

        private void collectEntityClasses(String host, Set<String> out) {
            ClassNode node = read(host);
            if (node == null) {
                return;
            }
            boolean factoryClass = node.interfaces.contains(FACTORY);
            Set<String> lambdaBodies = new HashSet<>();
            for (MethodNode method : node.methods) {
                if (method.instructions == null) {
                    continue;
                }
                for (AbstractInsnNode insn : method.instructions) {
                    if (!(insn instanceof InvokeDynamicInsnNode indy) || indy.bsm == null || indy.bsmArgs.length < 2
                            || !"java/lang/invoke/LambdaMetafactory".equals(indy.bsm.getOwner())
                            || !FACTORY.equals(Type.getReturnType(indy.desc).getInternalName())
                            || !(indy.bsmArgs[1] instanceof Handle handle)) {
                        continue;
                    }
                    if (handle.getTag() == Opcodes.H_NEWINVOKESPECIAL) {
                        out.add(handle.getOwner());
                    } else if (handle.getOwner().equals(node.name)) {
                        lambdaBodies.add(handle.getName() + handle.getDesc());
                    }
                }
            }
            for (MethodNode method : node.methods) {
                boolean body = lambdaBodies.contains(method.name + method.desc);
                if (!body && !(factoryClass && !method.name.equals("<init>") && !method.name.equals("<clinit>"))) {
                    continue;
                }
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof TypeInsnNode type && type.getOpcode() == Opcodes.NEW && warmable(type.desc)) {
                        out.add(type.desc);
                    }
                }
            }
        }

        private void warmGoals(String entity) {
            String current = entity;
            while (current != null && !current.equals(MOB) && !current.equals(LIVING) && warmable(current)) {
                if (!server.isRunning()) {
                    return;
                }
                ClassNode node = read(current);
                if (node == null) {
                    return;
                }
                if (scanned.add(current)) {
                    for (MethodNode method : node.methods) {
                        for (AbstractInsnNode insn : method.instructions) {
                            if (insn instanceof TypeInsnNode type && type.getOpcode() == Opcodes.NEW && warmable(type.desc)) {
                                link(type.desc);
                            }
                        }
                    }
                }
                current = node.superName;
            }
        }

        private boolean link(String internalName) {
            if (!linked.add(internalName)) {
                return true;
            }
            try {
                Class.forName(internalName.replace('/', '.'), false, loader).getDeclaredConstructors();
                return true;
            } catch (Throwable t) {
                failed++;
                return false;
            }
        }

        private ClassNode read(String internalName) {
            try (InputStream in = loader.getResourceAsStream(internalName + ".class")) {
                if (in == null) {
                    return null;
                }
                ClassNode node = new ClassNode();
                new ClassReader(in.readAllBytes()).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                return node;
            } catch (Throwable t) {
                return null;
            }
        }
    }

    private static boolean warmable(String internalName) {
        return !internalName.startsWith("java/") && !internalName.startsWith("javax/") && !internalName.startsWith("jdk/")
                && !internalName.startsWith("sun/") && !internalName.startsWith("org/") && !internalName.startsWith("com/google/")
                && !internalName.startsWith("net/minecraft/client/") && !internalName.startsWith("com/mojang/blaze3d/")
                && !internalName.contains("/client/") && !internalName.contains("/mixin/") && !internalName.contains("/mixins/");
    }
}
