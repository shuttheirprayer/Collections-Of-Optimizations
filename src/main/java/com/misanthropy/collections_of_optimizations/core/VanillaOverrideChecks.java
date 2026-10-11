package com.misanthropy.collections_of_optimizations.core;

import java.lang.reflect.Method;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.LevelReader;

public final class VanillaOverrideChecks {

    private static final Set<String> WET_CHECKS = Set.of("isInWaterRainOrBubble", "m_20071_", "isInWater", "m_20069_");

    private static final Set<String> FIRE_SETTERS = Set.of("setRemainingFireTicks", "m_7311_");

    private static final ClassValue<Boolean> STOCK_FIRE_SETTER = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                for (Class<?> c = type; c != null && c != Entity.class; c = c.getSuperclass()) {
                    for (Method method : c.getDeclaredMethods()) {
                        Class<?>[] params = method.getParameterTypes();
                        if (params.length == 1 && params[0] == int.class && FIRE_SETTERS.contains(method.getName())) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        }
    };

    private static final ClassValue<Boolean> STOCK_REMOVAL_TARGET = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                for (Class<?> c = type; c != null && c != RemoveBlockGoal.class; c = c.getSuperclass()) {
                    for (Method method : c.getDeclaredMethods()) {
                        Class<?>[] params = method.getParameterTypes();
                        if (method.getReturnType() == boolean.class && params.length == 2
                                && params[0] == LevelReader.class && params[1] == BlockPos.class) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        }
    };

    private static final ClassValue<Boolean> STOCK_WET_CHECK = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                for (Class<?> c = type; c != null && c != Entity.class; c = c.getSuperclass()) {
                    for (Method method : c.getDeclaredMethods()) {
                        if (method.getParameterCount() == 0 && WET_CHECKS.contains(method.getName())) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        }
    };

    private VanillaOverrideChecks() {
    }

    public static boolean stockRemovalTarget(Class<?> goalClass) {
        return STOCK_REMOVAL_TARGET.get(goalClass);
    }

    public static boolean stockFireSetter(Class<?> entityClass) {
        return STOCK_FIRE_SETTER.get(entityClass);
    }

    public static boolean stockWetCheck(Class<?> entityClass) {
        return STOCK_WET_CHECK.get(entityClass);
    }
}
