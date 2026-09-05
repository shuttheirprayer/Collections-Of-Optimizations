package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.misanthropy.collections_of_optimizations.core.FancyMenuIdentifierCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "de.keksuccino.fancymenu.customization.ScreenCustomization", remap = false)
public abstract class MixinScreenCustomization {

    @Inject(method = "reloadFancyMenu()V", at = @At("RETURN"), require = 0)
    private static void coo$invalidateOnReload(CallbackInfo ci) {
        FancyMenuIdentifierCache.invalidate();
    }

    @Inject(method = "init()V", at = @At("RETURN"), require = 0)
    private static void coo$invalidateOnInit(CallbackInfo ci) {
        FancyMenuIdentifierCache.invalidate();
    }
}
