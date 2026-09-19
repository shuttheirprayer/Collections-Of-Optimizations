package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.IdentityHashMap;
import java.util.Map;

public final class TransmogAppearanceCache {

    private static final String TAG_KEY = "transmog:transmogItem";
    private static final ResourceLocation HIDDEN_ID = new ResourceLocation("transmog", "void_fragment");
    private static final int MAX_ENTRIES = 256;

    private static final ThreadLocal<Map<CompoundTag, ItemStack>> CACHE =
            ThreadLocal.withInitial(IdentityHashMap::new);

    private static Item hiddenItem;

    private TransmogAppearanceCache() {
    }

    public static ItemStack resolve(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        CompoundTag tag;
        try {
            tag = stack.getTagElement(TAG_KEY);
        } catch (Throwable ignored) {
            return null;
        }
        if (tag == null) {
            return null;
        }

        Map<CompoundTag, ItemStack> map = CACHE.get();
        ItemStack appearance = map.get(tag);
        if (appearance != null) {
            return appearance;
        }

        try {
            appearance = ItemStack.of(tag);
        } catch (Throwable ignored) {
            return null;
        }
        if (appearance == null) {
            return null;
        }

        if (map.size() >= MAX_ENTRIES) {
            map.clear();
        }
        map.put(tag, appearance);
        return appearance;
    }

    public static boolean isHidden(ItemStack appearance) {
        if (appearance == null || appearance.isEmpty()) {
            return false;
        }
        Item item = hiddenItem;
        if (item == null) {
            try {
                Item resolved = BuiltInRegistries.ITEM.get(HIDDEN_ID);
                if (resolved != null && resolved != Items.AIR) {
                    hiddenItem = resolved;
                    item = resolved;
                }
            } catch (Throwable ignored) {
                return false;
            }
        }
        return item != null && appearance.is(item);
    }
}
