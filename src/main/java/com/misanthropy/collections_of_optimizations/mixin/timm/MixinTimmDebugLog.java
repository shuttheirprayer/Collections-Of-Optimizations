package com.misanthropy.collections_of_optimizations.mixin.timm;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.github.charlyb01.timm.Timm", remap = false)
public abstract class MixinTimmDebugLog {

    @Redirect(method = "debugLog", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    private static void coo$debugLogToDebug(Logger logger, String message) {
        if (CoOConfig.timmDebugLogToDebug) {
            logger.debug(message);
        } else {
            logger.info(message);
        }
    }
}
