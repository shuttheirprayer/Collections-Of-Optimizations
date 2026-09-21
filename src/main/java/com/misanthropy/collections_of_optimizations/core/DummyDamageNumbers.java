package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public final class DummyDamageNumbers {

    private static final int UNRESOLVED = 0;
    private static final int READY = 1;
    private static final int FAILED = 2;

    private static final ResourceLocation TRUE_DAMAGE = new ResourceLocation("dummmmmmy", "true");

    private static volatile int state = UNRESOLVED;

    private static Supplier<?> damageMode;
    private static Supplier<?> healMode;
    private static Object noneMode;
    private static Object allPlayersMode;
    private static Object localPlayerMode;

    private DummyDamageNumbers() {
    }

    public static ResourceLocation trueDamage() {
        return TRUE_DAMAGE;
    }

    public static boolean skipDamageNumber(boolean attackerIsServerPlayer) {
        if (!resolve()) {
            return false;
        }
        Object mode;
        try {
            mode = damageMode.get();
        } catch (Throwable throwable) {
            return false;
        }
        return unusedMode(mode, attackerIsServerPlayer);
    }

    public static boolean skipHealNumber(boolean targetIsServerPlayer) {
        if (!resolve()) {
            return false;
        }
        Object mode;
        try {
            mode = healMode.get();
        } catch (Throwable throwable) {
            return false;
        }
        return unusedMode(mode, targetIsServerPlayer);
    }

    private static boolean unusedMode(Object mode, boolean hasServerPlayer) {
        if (mode == null) {
            return false;
        }
        if (mode == noneMode) {
            return true;
        }
        return !hasServerPlayer && (mode == allPlayersMode || mode == localPlayerMode);
    }

    private static boolean resolve() {
        int current = state;
        if (current == READY) {
            return true;
        }
        if (current == FAILED) {
            return false;
        }
        return resolveSlow();
    }

    private static synchronized boolean resolveSlow() {
        if (state == READY) {
            return true;
        }
        if (state == FAILED) {
            return false;
        }
        try {
            ClassLoader loader = DummyDamageNumbers.class.getClassLoader();
            Class<?> configs = Class.forName("net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs", true, loader);
            Class<?> modes = Class.forName("net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs$Mode", true, loader);
            Field damageField = configs.getField("DAMAGE_NUMBERS_MODE");
            Field healField = configs.getField("HEALING_NUMBERS_MODE");
            Object damageSupplier = damageField.get(null);
            Object healSupplier = healField.get(null);
            Object none = null;
            Object allPlayers = null;
            Object localPlayer = null;
            Object[] constants = modes.getEnumConstants();
            if (constants != null) {
                for (Object constant : constants) {
                    String name = ((Enum<?>) constant).name();
                    if ("NONE".equals(name)) {
                        none = constant;
                    } else if ("ALL_PLAYERS".equals(name)) {
                        allPlayers = constant;
                    } else if ("LOCAL_PLAYER".equals(name)) {
                        localPlayer = constant;
                    }
                }
            }
            if (none == null || !(damageSupplier instanceof Supplier) || !(healSupplier instanceof Supplier)) {
                state = FAILED;
                return false;
            }
            damageMode = (Supplier<?>) damageSupplier;
            healMode = (Supplier<?>) healSupplier;
            noneMode = none;
            allPlayersMode = allPlayers;
            localPlayerMode = localPlayer;
            state = READY;
            return true;
        } catch (Throwable throwable) {
            state = FAILED;
            return false;
        }
    }
}
