package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class DomesticationEnchantCache {

    private static final AtomicInteger GENERATION = new AtomicInteger();
    private static final Map<Enchantment, String> KEYS = new ConcurrentHashMap<>();
    private static final ThreadLocal<Slot> SLOT = ThreadLocal.withInitial(Slot::new);

    private DomesticationEnchantCache() {
    }

    public static void invalidate() {
        GENERATION.incrementAndGet();
    }

    public static String keyOf(Enchantment enchantment) {
        String cached = KEYS.get(enchantment);
        if (cached != null) {
            return cached;
        }
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (id == null) {
            return null;
        }
        String value = id.toString();
        KEYS.put(enchantment, value);
        return value;
    }

    public static int levelOf(ListTag list, String key) {
        if (list == null || key == null) {
            return 0;
        }
        Slot slot = SLOT.get();
        slot.refresh(list);
        Integer level = slot.levels.get(key);
        return level == null ? 0 : level;
    }

    private static final class Slot {

        private final Map<String, Integer> levels = new HashMap<>();
        private ListTag list;
        private int size = -1;
        private int generation = -1;

        private void refresh(ListTag current) {
            int gen = GENERATION.get();
            int currentSize = current.size();
            if (current == list && currentSize == size && gen == generation) {
                return;
            }
            levels.clear();
            for (int i = 0; i < currentSize; i++) {
                CompoundTag entry = current.getCompound(i);
                ResourceLocation id = EnchantmentHelper.getEnchantmentId(entry);
                if (id == null) {
                    continue;
                }
                levels.putIfAbsent(id.toString(), EnchantmentHelper.getEnchantmentLevel(entry));
            }
            list = current;
            size = currentSize;
            generation = gen;
        }
    }
}
