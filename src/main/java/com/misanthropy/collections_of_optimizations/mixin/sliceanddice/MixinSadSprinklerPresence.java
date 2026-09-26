package com.misanthropy.collections_of_optimizations.mixin.sliceanddice;

import com.misanthropy.collections_of_optimizations.core.SliceAndDiceWetAir;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.possible_triangle.sliceanddice.block.sprinkler.SprinklerTile", remap = false)
public abstract class MixinSadSprinklerPresence {

    @Inject(method = "<init>", at = @At("RETURN"), remap = false, require = 0)
    private void coo$markSprinkler(CallbackInfo ci) {
        SliceAndDiceWetAir.markSprinkler();
    }
}
