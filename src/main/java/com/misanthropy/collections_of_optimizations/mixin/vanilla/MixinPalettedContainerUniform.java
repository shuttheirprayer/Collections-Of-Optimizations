package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.SingleValuePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reimplemented from Lomka 0.5.4 by Starlev (LGPL-3.0). */
@Mixin(PalettedContainer.class)
public abstract class MixinPalettedContainerUniform<T> {

    @Shadow
    private volatile PalettedContainer.Data<T> data;

    @Inject(method = "get(I)Ljava/lang/Object;", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$uniformValue(int index, CallbackInfoReturnable<T> cir) {
        if (CoOConfig.vanillaUniformSectionLookup && this.data.palette() instanceof SingleValuePalette<T> palette && palette.value != null) {
            cir.setReturnValue(palette.value);
        }
    }
}
