package com.misanthropy.collections_of_optimizations.core;

import com.github.alexthe666.citadel.ServerProxy;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public final class ClientLevelRelease {

    private static final String[][] SERVER_STATICS = {
            {"opposing_force", "com.unusualmodding.opposing_force.utils.CommonProxy", "SKYVERN_SPAWNER_MAP"},
            {"kubejs", "dev.latvian.mods.kubejs.server.KubeJSReloadListener", "resources"},
    };

    private static final Map<Object, String> SERVER_HOLDERS = Collections.synchronizedMap(new WeakHashMap<>());

    private static Field simplySwordsGloamWorld;

    private ClientLevelRelease() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ClientLevelRelease::onServerStopped);
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        if (ModList.get().isLoaded("simplyswords")) {
            simplySwordsGloamWorld = staticField("net.sweenus.simplyswords.client.renderer.ObserverStatusVisualRenderer", "gloamWorld");
        }
        MinecraftForge.EVENT_BUS.addListener(ClientLevelRelease::onLoggingOut);
    }

    public static void trackServerHolder(Object holder, String field) {
        SERVER_HOLDERS.put(holder, field);
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        if (!CoOConfig.releaseStoppedServer) {
            return;
        }
        ZetaWrapperMemo.forgetEvents();
        if (ModList.get().isLoaded("citadel")) {
            ServerProxy.setMinecraftServer(null);
        }
        if (ModList.get().isLoaded("copycats")) {
            try {
                Method setServer = Class.forName("com.copycatsplus.copycats.utility.LogicalSidedProvider").getMethod("setServer", Supplier.class);
                Supplier<MinecraftServer> current = ServerLifecycleHooks::getCurrentServer;
                setServer.invoke(null, current);
            } catch (ReflectiveOperationException | LinkageError ignored) {
            }
        }
        for (String[] entry : SERVER_STATICS) {
            if (ModList.get().isLoaded(entry[0])) {
                Field field = staticField(entry[1], entry[2]);
                if (field != null) {
                    release(field, null);
                }
            }
        }
        Object[] holders;
        synchronized (SERVER_HOLDERS) {
            holders = SERVER_HOLDERS.entrySet().toArray();
        }
        for (Object holder : holders) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) holder;
            Object owner = entry.getKey();
            if (owner == null) {
                continue;
            }
            try {
                Field field = owner.getClass().getDeclaredField((String) entry.getValue());
                field.setAccessible(true);
                field.set(owner, null);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ZetaWrapperMemo.forgetEvents();
        if (ModList.get().isLoaded("subtle_effects")) {
            SubtleEffectsDisplayTick.release();
        }
        if (simplySwordsGloamWorld != null) {
            release(simplySwordsGloamWorld, null);
        }
    }

    private static Field staticField(String owner, String name) {
        try {
            Field field = Class.forName(owner).getDeclaredField(name);
            if (!Modifier.isStatic(field.getModifiers())) {
                return null;
            }
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }

    private static void release(Field field, Object owner) {
        try {
            Object value = field.get(owner);
            if (value instanceof Map<?, ?> map) {
                map.clear();
            } else if (value instanceof Collection<?> collection) {
                collection.clear();
            } else if (value != null && !Modifier.isFinal(field.getModifiers())) {
                field.set(owner, null);
            }
        } catch (IllegalAccessException | RuntimeException ignored) {
        }
    }
}
