package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public final class MahouRipperEffect {

    private static final ResourceLocation ID = new ResourceLocation("mahoutsukai", "ripper_invis");

    private static volatile MobEffect resolved;

    private MahouRipperEffect() {
    }

    public static MobEffect invisibility() {
        MobEffect effect = resolved;
        if (effect == null) {
            effect = ForgeRegistries.MOB_EFFECTS.getValue(ID);
            resolved = effect;
        }
        return effect;
    }
}
