package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class L2ComplementsTags {

    public static final TagKey<DamageType> IS_MAGIC =
            TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("forge", "is_magic"));

    private L2ComplementsTags() {
    }
}
