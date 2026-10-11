package com.misanthropy.collections_of_optimizations.mixin.ftbchunks;

import com.misanthropy.collections_of_optimizations.core.FtbRegionImageGen;
import dev.ftb.mods.ftbchunks.client.map.RenderMapImageTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderMapImageTask.class, remap = false)
public abstract class MixinRenderMapImageTask {

    @Inject(method = "runMapTask()V", at = @At("HEAD"), require = 0)
    private void coo$imageRenderStarted(CallbackInfo ci) {
        if (((RenderMapImageTask) (Object) this).region instanceof FtbRegionImageGen gen) {
            gen.coo$bumpImageGen();
        }
    }
}
