package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class JneAntidoteEffectCache {

    private static final String ANTIDOTE_EFFECT = "AntidoteEffect";
    private static final String ANTIDOTE = "Antidote";
    private static final String AWKWARD = "Awkward";

    private static final int MAX_ENTRIES = 256;

    private static final Map<String, Optional<MobEffect>> CACHE = new ConcurrentHashMap<>();

    private JneAntidoteEffectCache() {
    }

    public static MobEffect resolve(CompoundTag tag) {
        if (tag == null || !tag.contains(ANTIDOTE_EFFECT) || AWKWARD.equals(tag.getString(ANTIDOTE))) {
            return null;
        }
        String id = tag.getString(ANTIDOTE_EFFECT);
        Optional<MobEffect> cached = CACHE.get(id);
        if (cached == null) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            cached = Optional.ofNullable(location == null ? null : BuiltInRegistries.MOB_EFFECT.get(location));
            if (CACHE.size() < MAX_ENTRIES) {
                CACHE.put(id, cached);
            }
        }
        return cached.orElse(null);
    }
}
