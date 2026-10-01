package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.DensityGraphReuse;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunctions.HolderHolder.class)
public abstract class MixinHolderHolderReuse {

    @Inject(method = "mapAll", at = @At("HEAD"), cancellable = true)
    private void coo$reuseMappedHolder(DensityFunction.Visitor visitor, CallbackInfoReturnable<DensityFunction> cir) {
        if (visitor instanceof DensityGraphReuse.FirstPass pass) {
            DensityFunction mapped = pass.mappedHolder(((DensityFunctions.HolderHolder) (Object) this).function().value());
            if (mapped != null) {
                cir.setReturnValue(mapped);
            }
        }
    }

    @Inject(method = "mapAll", at = @At("RETURN"))
    private void coo$rememberMappedHolder(DensityFunction.Visitor visitor, CallbackInfoReturnable<DensityFunction> cir) {
        if (visitor instanceof DensityGraphReuse.FirstPass pass) {
            pass.rememberHolder(((DensityFunctions.HolderHolder) (Object) this).function().value(), cir.getReturnValue());
        }
    }
}
