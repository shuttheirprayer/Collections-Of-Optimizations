package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FlatClimateTree;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.biome.Climate$RTree")
public abstract class MixinClimateRTreeFlatSearch {
    @Unique
    private volatile FlatClimateTree coo$flat;

    @Inject(method = "search", at = @At("HEAD"), cancellable = true)
    private void coo$flatSearch(Climate.TargetPoint target, @Coerce Object metric, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.vanillaFlatBiomeSearch || FlatClimateTree.checking()) {
            return;
        }
        FlatClimateTree flat = coo$flat;
        if (flat == null) {
            flat = coo$build(metric);
        }
        if (!flat.usable() || !flat.acceptsMetric(metric)) {
            return;
        }
        Object found = flat.search(target);
        if (found != null) {
            cir.setReturnValue(found);
        }
    }

    @Unique
    private synchronized FlatClimateTree coo$build(Object metric) {
        FlatClimateTree flat = coo$flat;
        if (flat == null) {
            flat = FlatClimateTree.build(this, metric);
            coo$flat = flat;
        }
        return flat;
    }
}
