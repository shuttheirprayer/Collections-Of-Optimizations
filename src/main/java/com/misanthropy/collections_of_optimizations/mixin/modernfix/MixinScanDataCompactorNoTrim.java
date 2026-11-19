package com.misanthropy.collections_of_optimizations.mixin.modernfix;

import org.embeddedt.modernfix.forge.load.ModFileScanDataCompactor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@Mixin(value = ModFileScanDataCompactor.class, remap = false)
public abstract class MixinScanDataCompactorNoTrim {

    @Redirect(
            method = "lambda$compact$1",
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;trimToSize()V"),
            require = 0
    )
    private static void coo$leaveSharedListAlone(ArrayList<?> list) {
    }
}
