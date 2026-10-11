package com.misanthropy.collections_of_optimizations.mixin.ftbchunks;

import com.misanthropy.collections_of_optimizations.core.FtbRegionImageGen;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MapRegion.class, remap = false)
public abstract class MixinMapRegionImageGen implements FtbRegionImageGen {

    @Unique
    private volatile int coo$imageGen;

    @Override
    public int coo$imageGen() {
        return coo$imageGen;
    }

    @Override
    public void coo$bumpImageGen() {
        coo$imageGen++;
    }

    @Inject(method = "afterImageRenderTask()V", at = @At("HEAD"), require = 0)
    private void coo$imageRendered(CallbackInfo ci) {
        coo$imageGen++;
    }
}
