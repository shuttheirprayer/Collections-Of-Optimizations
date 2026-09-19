package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;
import java.util.Map;

public final class MoreArtifactsMapPurge {

    private static final String OWNER = "net.gobies.moreartifacts.event.DamageEvents";

    private static final String[] FIELDS = {
            "generalDamageReductionMap",
            "fireDamageReductionMap",
            "generalDamageIncreaseMap",
            "equippedArtifactsMap",
            "lastHealTimeMap"
    };

    private static volatile Map<?, ?>[] maps;
    private static volatile boolean failed;

    private MoreArtifactsMapPurge() {
    }

    public static void sweep() {
        if (failed) {
            return;
        }
        Map<?, ?>[] targets = maps;
        if (targets == null) {
            targets = resolve();
            if (targets == null) {
                failed = true;
                return;
            }
            maps = targets;
        }
        for (Map<?, ?> map : targets) {
            if (map.isEmpty()) {
                continue;
            }
            try {
                map.keySet().removeIf(key -> !(key instanceof Entity entity) || entity.isRemoved());
            } catch (RuntimeException exception) {
                return;
            }
        }
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
