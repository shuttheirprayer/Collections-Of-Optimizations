package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.lang.reflect.Field;
import java.util.Map;

public final class MahouInsightTargetPurge {

    private static final String OWNER = "stepsword.mahoutsukai.potion.InsightEyesPotion";
    private static final String FIELD = "hadTargets";

    private static volatile Map<?, ?> targets;
    private static volatile boolean failed;

    private MahouInsightTargetPurge() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(MahouInsightTargetPurge::onLoggedOut);
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!CoOConfig.mahoutsukaiPurgeInsightTargets || failed) {
            return;
        }
        Map<?, ?> map = targets;
        if (map == null) {
            map = resolve();
            if (map == null) {
                failed = true;
                return;
            }
            targets = map;
        }
        if (map.isEmpty()) {
            return;
        }
        try {
            map.keySet().remove(event.getEntity());
            map.keySet().removeIf(key -> !(key instanceof Entity entity) || entity.isRemoved());
        } catch (RuntimeException exception) {
            failed = true;
        }
    }

    private static Map<?, ?> resolve() {
        try {
            Field field = Class.forName(OWNER).getDeclaredField(FIELD);
            field.setAccessible(true);
            Object value = field.get(null);
            return value instanceof Map<?, ?> map ? map : null;
        } catch (Throwable throwable) {
            return null;
        }
    }
}
