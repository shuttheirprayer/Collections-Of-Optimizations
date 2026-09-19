package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.oddaccessories.configuration.ConfigConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class OddAccessoriesRewardPool {

    private static volatile List<Item> pool;
    private static volatile Object blacklistKey;
    private static volatile Object disabledKey;

    private OddAccessoriesRewardPool() {
    }

    public static List<Item> pool(Set<String> excluded) {
        if (excluded == null || !ConfigConfiguration.SPEC.isLoaded()) {
            return null;
        }

        List<? extends String> blacklist;
        List<? extends String> disabled;
        try {
            blacklist = ConfigConfiguration.BAGBLACKLIST.get();
            disabled = ConfigConfiguration.DISABLEDCURIOS.get();
        } catch (RuntimeException exception) {
            return null;
        }

        List<Item> cached = pool;
        if (cached != null && blacklistKey == blacklist && disabledKey == disabled) {
            return cached;
        }

        List<Item> built = new ArrayList<>();
        for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (!id.getNamespace().equals("oddaccessories")) {
                continue;
            }
            if (excluded.contains(id.getPath())) {
                continue;
            }
            if (blacklist.contains(id.toString())) {
                continue;
            }
            if (ConfigConfiguration.isDisabled(entry.getValue())) {
                continue;
            }
            built.add(entry.getValue());
        }

        List<Item> frozen = List.copyOf(built);
        blacklistKey = blacklist;
        disabledKey = disabled;
        pool = frozen;
        return frozen;
    }
}
