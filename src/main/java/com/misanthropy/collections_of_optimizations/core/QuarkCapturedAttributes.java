package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.item.ItemStack;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;

public final class QuarkCapturedAttributes {

    private static final VarHandle FIELD = find();

    private QuarkCapturedAttributes() {
    }

    private static VarHandle find() {
        try {
            return MethodHandles.privateLookupIn(ItemStack.class, MethodHandles.lookup()).findVarHandle(ItemStack.class, "capturedAttributes", Map.class);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    public static boolean ready() {
        return FIELD != null;
    }

    public static void ensure(ItemStack stack) {
        if (FIELD != null && FIELD.get(stack) == null) {
            FIELD.set(stack, new HashMap<>());
        }
    }
}
