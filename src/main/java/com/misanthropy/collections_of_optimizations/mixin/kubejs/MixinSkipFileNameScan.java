package com.misanthropy.collections_of_optimizations.mixin.kubejs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Pseudo
@Mixin(targets = "dev.latvian.mods.kubejs.script.data.GeneratedResourcePack", remap = false)
public abstract class MixinSkipFileNameScan {

    @Inject(method = "scanForInvalidFiles", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipFileNameScan(String pathName, Path path, CallbackInfo ci) {
        if (CoOConfig.kubejsSkipFileNameScan) {
            ci.cancel();
        }
    }
}
