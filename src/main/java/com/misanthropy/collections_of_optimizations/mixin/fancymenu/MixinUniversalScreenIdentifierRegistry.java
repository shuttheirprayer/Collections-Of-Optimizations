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
@Mixin(targets = "de.keksuccino.fancymenu.customization.screen.identifier.UniversalScreenIdentifierRegistry", remap = false)
public abstract class MixinUniversalScreenIdentifierRegistry {

    @WrapMethod(method = "getUniversalIdentifierFor(Ljava/lang/String;)Ljava/lang/String;", require = 0)
    private static String coo$memoUniversalIdentifier(String identifier, Operation<String> original) {
        if (!CoOConfig.fancymenuCacheScreenIdentifiers || identifier == null || !FancyMenuIdentifierCache.usable()) {
            return original.call(identifier);
        }

        String cached = FancyMenuIdentifierCache.getUniversal(identifier);
        if (cached != null) {
            return FancyMenuIdentifierCache.isNull(cached) ? null : cached;
        }

        String universal = original.call(identifier);
        FancyMenuIdentifierCache.putUniversal(identifier, universal);
        return universal;
    }

    @Inject(method = "register(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"), require = 0)
    private static void coo$invalidateOnRegister(String universalIdentifier, String screenIdentifier, CallbackInfo ci) {
        FancyMenuIdentifierCache.invalidate();
    }
}
