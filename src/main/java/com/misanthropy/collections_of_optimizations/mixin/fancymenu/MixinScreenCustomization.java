package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
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

    @Inject(method = "addScreenBlacklistRule", at = @At("RETURN"), require = 0)
    private static void coo$invalidateOnBlacklistRule(CallbackInfo ci) {
        FancyMenuIdentifierCache.invalidate();
    }

    @WrapMethod(method = "isScreenBlacklisted(Ljava/lang/String;)Z", require = 0)
    private static boolean coo$memoScreenBlacklist(String screenClassPath, Operation<Boolean> original) {
        if (!CoOConfig.fancymenuCacheScreenBlacklist || screenClassPath == null || !FancyMenuIdentifierCache.usable()) {
            return original.call(screenClassPath);
        }

        byte cached = FancyMenuIdentifierCache.getBlacklisted(screenClassPath);
        if (cached != FancyMenuIdentifierCache.UNKNOWN) {
            return cached != 0;
        }

        boolean blacklisted = original.call(screenClassPath);
        FancyMenuIdentifierCache.putBlacklisted(screenClassPath, blacklisted);
        return blacklisted;
    }
}
