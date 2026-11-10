package com.misanthropy.collections_of_optimizations.mixin.cablefacades;

import com.misanthropy.collections_of_optimizations.core.CableFacadeServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.portingdeadmods.cable_facades.data.CableFacadeSavedData", remap = false)
public abstract class MixinCableFacadeSavedDataVersion {

    @Inject(method = "getOrCreateFacadeMapForChunk", at = @At("RETURN"), require = 0)
    private void coo$noteChunkMap(CallbackInfoReturnable<?> cir) {
        CableFacadeServerData.chunkMapCreated();
    }
}
