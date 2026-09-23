package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class L2BackpackBagCount {

    private static final int BASE_SIZE = 64;

    private L2BackpackBagCount() {
    }

    public static int sizeFactor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 1;
        }
        return Math.max(1, tag.getInt("BagUpgrade"));
    }

    public static int occupied(ItemStack stack) {
        CompoundTag root = stack.getTagElement("BlockEntityTag");
        if (root == null) {
            return 0;
        }

        ListTag items = root.getList("Items", Tag.TAG_COMPOUND);
        int size = items.size();
        if (size == 0) {
            return 0;
        }

        int invSize = BASE_SIZE * sizeFactor(stack);
        long[] filled = new long[(invSize + 63) >> 6];

        for (int i = 0; i < size; i++) {
            CompoundTag entry = items.getCompound(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot >= invSize) {
                continue;
            }
            int word = slot >> 6;
            long bit = 1L << (slot & 63);
            if (isPresent(entry)) {
                filled[word] |= bit;
            } else {
                filled[word] &= ~bit;
            }
        }

        int count = 0;
        for (long word : filled) {
            count += Long.bitCount(word);
        }
        return count;
    }

    private static boolean isPresent(CompoundTag entry) {
        if (entry.getByte("Count") <= 0) {
            return false;
        }
        ResourceLocation id = ResourceLocation.tryParse(entry.getString("id"));
        if (id == null) {
            return false;
        }
        Item item = BuiltInRegistries.ITEM.get(id);
        return item != null && item != Items.AIR;
    }
}
