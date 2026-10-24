package com.misanthropy.collections_of_optimizations.mixin.brutality;

import net.goo.brutality.util.helpers.EnvironmentColorManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(value = Biome.class, priority = 1100)
public abstract class MixinBrutalityBiomeColors {

    @Shadow
    @Final
    private BiomeSpecialEffects specialEffects;

    @Shadow
    protected abstract int getGrassColorFromTexture();

    @Shadow
    protected abstract int getFoliageColorFromTexture();

    @Overwrite
    public int getGrassColor(double x, double z) {
        Integer forced = EnvironmentColorManager.getColorOverride(EnvironmentColorManager.ColorType.GRASS);
        if (forced != null) {
            return forced;
        }
        Optional<Integer> fixed = this.specialEffects.getGrassColorOverride();
        int color = fixed.isPresent() ? fixed.get() : this.getGrassColorFromTexture();
        BiomeSpecialEffects.GrassColorModifier modifier = this.specialEffects.getGrassColorModifier();
        return modifier == BiomeSpecialEffects.GrassColorModifier.NONE ? color : modifier.modifyColor(x, z, color);
    }

    @Overwrite
    public int getFoliageColor() {
        Integer forced = EnvironmentColorManager.getColorOverride(EnvironmentColorManager.ColorType.FOLIAGE);
        if (forced != null) {
            return forced;
        }
        Optional<Integer> fixed = this.specialEffects.getFoliageColorOverride();
        return fixed.isPresent() ? fixed.get() : this.getFoliageColorFromTexture();
    }
}
