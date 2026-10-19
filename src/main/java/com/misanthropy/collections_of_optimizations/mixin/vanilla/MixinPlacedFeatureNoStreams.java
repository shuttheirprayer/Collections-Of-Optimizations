package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FeaturePlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlacedFeature.class)
public abstract class MixinPlacedFeatureNoStreams {

    @Shadow
    @Final
    private Holder<ConfiguredFeature<?, ?>> feature;

    @Shadow
    @Final
    private List<PlacementModifier> placement;

    @Inject(
            method = "placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$placeWithoutStreams(PlacementContext context, RandomSource random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.vanillaPlaceFeaturesWithoutStreams || FeaturePlacement.otherRewriteActive()) {
            return;
        }
        cir.setReturnValue(FeaturePlacement.place(this.placement, this.feature.value(), context, random, pos));
    }
}
