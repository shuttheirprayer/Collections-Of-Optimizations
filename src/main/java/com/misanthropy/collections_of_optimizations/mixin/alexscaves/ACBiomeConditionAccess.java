package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BiomeGenerationNoiseCondition.class, remap = false)
public interface ACBiomeConditionAccess {

    @Accessor("dimensions")
    List<String> coo$getDimensions();
}
