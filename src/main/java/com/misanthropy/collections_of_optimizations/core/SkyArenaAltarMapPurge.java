package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.lang.reflect.Field;
import java.util.Map;

public final class SkyArenaAltarMapPurge {

    private static final String OWNER = "net.jrdemiurge.skyarena.block.entity.AltarBlockEntity";

    private static final String[] FIELDS = {
            "activeAltarBlocks",
            "playerMessageTimestamps"
    };

    private static volatile Map<?, ?>[] maps;
    private static volatile boolean failed;

    private SkyArenaAltarMapPurge() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SkyArenaAltarMapPurge::onLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SkyArenaAltarMapPurge::onClone);
        MinecraftForge.EVENT_BUS.addListener(SkyArenaAltarMapPurge::onServerStopped);
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        purge(event.getEntity());
    }

    private static void onClone(PlayerEvent.Clone event) {
        purge(event.getOriginal());
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        if (!CoOConfig.skyarenaPruneAltarPlayerMaps) {
            return;
        }
        Map<?, ?>[] targets = targets();
        if (targets == null) {
            return;
        }
        for (Map<?, ?> map : targets) {
            try {
                map.clear();
            } catch (RuntimeException exception) {
                failed = true;
                return;
            }
        }
    }

    private static void purge(Entity leaving) {
        if (!CoOConfig.skyarenaPruneAltarPlayerMaps) {
            return;
        }
        Map<?, ?>[] targets = targets();
        if (targets == null) {
            return;
        }
        for (Map<?, ?> map : targets) {
            if (map.isEmpty()) {
                continue;
            }
            try {
                if (leaving != null) {
                    map.keySet().remove(leaving);
                }
                map.keySet().removeIf(key -> !(key instanceof Entity entity) || entity.isRemoved());
            } catch (RuntimeException exception) {
                failed = true;
                return;
            }
        }
    }

    private static Map<?, ?>[] targets() {
        if (failed) {
            return null;
        }
        Map<?, ?>[] resolved = maps;
        if (resolved != null) {
            return resolved;
        }
        resolved = resolve();
        if (resolved == null) {
            failed = true;
            return null;
        }
        maps = resolved;
        return resolved;
    }

    private static Map<?, ?>[] resolve() {
        try {
            Class<?> owner = Class.forName(OWNER);
            Map<?, ?>[] resolved = new Map[FIELDS.length];
            for (int i = 0; i < FIELDS.length; i++) {
                Field field = owner.getDeclaredField(FIELDS[i]);
                field.setAccessible(true);
                Object value = field.get(null);
                if (!(value instanceof Map<?, ?> map)) {
                    return null;
                }
                resolved[i] = map;
            }
            return resolved;
        } catch (Throwable throwable) {
            return null;
        }
    }
}
