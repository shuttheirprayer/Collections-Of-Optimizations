package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;

public final class PersistentKeys {

    private PersistentKeys() {
    }

    public static boolean lacks(CompoundTag tag, String key) {
        return tag == null || !tag.contains(key);
    }

    public static boolean lacks(CompoundTag tag, String first, String second) {
        return tag == null || (!tag.contains(first) && !tag.contains(second));
    }

    public static boolean lacks(CompoundTag tag, String first, String second, String third) {
        return tag == null || (!tag.contains(first) && !tag.contains(second) && !tag.contains(third));
    }
}
