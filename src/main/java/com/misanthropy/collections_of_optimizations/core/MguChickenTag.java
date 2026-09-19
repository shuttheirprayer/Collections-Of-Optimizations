package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public final class MguChickenTag {

    private static final CompoundTag EMPTY = new CompoundTag();

    private static final MethodHandle GETTER = resolve();

    private MguChickenTag() {
    }

    private static MethodHandle resolve() {
        try {
            Field field = Entity.class.getDeclaredField("persistentData");
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectGetter(field);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static CompoundTag existing(Entity entity) {
        MethodHandle getter = GETTER;
        if (getter == null || entity == null) {
            return null;
        }
        try {
            CompoundTag tag = (CompoundTag) getter.invoke(entity);
            return tag == null ? EMPTY : tag;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
