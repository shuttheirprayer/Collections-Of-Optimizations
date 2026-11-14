package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.core.ParameterListEncodeCache;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeSourceEncodeCache {

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Climate$ParameterList;codec(Lcom/mojang/serialization/MapCodec;)Lcom/mojang/serialization/Codec;"
            ),
            require = 0
    )
    private static Codec<Climate.ParameterList<Holder<Biome>>> coo$cacheBiomeListEncoding(Codec<Climate.ParameterList<Holder<Biome>>> codec) {
        return ParameterListEncodeCache.wrap(codec);
    }
}
