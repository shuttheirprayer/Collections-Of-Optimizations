package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.lang.reflect.Method;
import java.util.Optional;

public final class AnsCooldownCarry {

    private static volatile boolean resolved;
    private static volatile boolean available;
    private static volatile Capability<?> capability;
    private static volatile Method saveMethod;
    private static volatile Method loadMethod;

    private AnsCooldownCarry() {
    }

    public static void copy(Player original, Player clone) {
        if (original == null || clone == null || original == clone) {
            return;
        }
        if (!resolve()) {
            return;
        }
        try {
            original.reviveCaps();
            Object from = data(original);
            if (from == null) {
                return;
            }
            Object to = data(clone);
            if (to == null) {
                return;
            }
            CompoundTag tag = new CompoundTag();
            saveMethod.invoke(from, tag);
            loadMethod.invoke(to, tag);
        } catch (Throwable throwable) {
            available = false;
        }
    }

    private static Object data(Player player) {
        LazyOptional<?> holder = player.getCapability(capability);
        if (holder == null) {
            return null;
        }
        Optional<?> value = holder.resolve();
        return value.orElse(null);
    }

    private static synchronized boolean resolve() {
        if (resolved) {
            return available;
        }
        resolved = true;
        try {
            Class<?> type = Class.forName("com.otectus.arsnspells.data.CooldownData");
            Object token = type.getField("COOLDOWN_CAP").get(null);
            if (!(token instanceof Capability)) {
                return false;
            }
            Method save = type.getMethod("save", CompoundTag.class);
            Method load = type.getMethod("load", CompoundTag.class);
            capability = (Capability<?>) token;
            saveMethod = save;
            loadMethod = load;
            available = true;
        } catch (Throwable throwable) {
            available = false;
        }
        return available;
    }
}
