package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

public final class HnnBrokenModelProbe {

    private static volatile Method storedModel;

    private static volatile Method bound;

    private static volatile boolean resolved;

    private static volatile boolean usable;

    private HnnBrokenModelProbe() {
    }

    public static boolean isUnresolved(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!resolved) {
            resolve();
        }
        if (!usable) {
            return false;
        }
        try {
            Object holder = storedModel.invoke(null, stack);
            return holder == null || !((Boolean) bound.invoke(holder));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }
        try {
            Class<?> dataModelItem = Class.forName(
                    "dev.shadowsoffire.hostilenetworks.item.DataModelItem",
                    false,
                    HnnBrokenModelProbe.class.getClassLoader());
            Method holderGetter = dataModelItem.getMethod("getStoredModel", ItemStack.class);
            Method boundGetter = holderGetter.getReturnType().getMethod("isBound");
            holderGetter.setAccessible(true);
            boundGetter.setAccessible(true);
            storedModel = holderGetter;
            bound = boundGetter;
            usable = true;
        } catch (Throwable ignored) {
            usable = false;
        } finally {
            resolved = true;
        }
    }
}
