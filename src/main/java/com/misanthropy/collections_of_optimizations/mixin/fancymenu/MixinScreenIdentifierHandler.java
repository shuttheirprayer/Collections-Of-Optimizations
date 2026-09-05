package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FancyMenuIdentifierCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "de.keksuccino.fancymenu.customization.screen.identifier.ScreenIdentifierHandler", remap = false)
public abstract class MixinScreenIdentifierHandler {

    @WrapMethod(method = "isValidIdentifier(Ljava/lang/String;)Z", require = 0)
    private static boolean coo$memoValidIdentifier(String identifier, Operation<Boolean> original) {
        if (!CoOConfig.fancymenuCacheScreenIdentifiers || identifier == null || !FancyMenuIdentifierCache.usable()) {
            return original.call(identifier);
        }

        byte cached = FancyMenuIdentifierCache.getValid(identifier);
        if (cached != FancyMenuIdentifierCache.UNKNOWN) {
            return cached != 0;
        }

        boolean valid = original.call(identifier);
        FancyMenuIdentifierCache.putValid(identifier, valid);
        return valid;
    }

    @WrapMethod(method = "getBestIdentifier(Ljava/lang/String;)Ljava/lang/String;", require = 0)
    private static String coo$memoBestIdentifier(String identifier, Operation<String> original) {
        if (!CoOConfig.fancymenuCacheScreenIdentifiers || identifier == null || !FancyMenuIdentifierCache.usable()) {
            return original.call(identifier);
        }

        String cached = FancyMenuIdentifierCache.getBest(identifier);
        if (cached != null) {
            return FancyMenuIdentifierCache.isNull(cached) ? null : cached;
        }

        String best = original.call(identifier);
        FancyMenuIdentifierCache.putBest(identifier, best);
        return best;
    }
}
