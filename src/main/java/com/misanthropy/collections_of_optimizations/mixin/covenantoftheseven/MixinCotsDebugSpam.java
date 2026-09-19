package com.misanthropy.collections_of_optimizations.mixin.covenantoftheseven;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(targets = "net.llenzzz.covenant_of_the_seven.events.item.virtue.MelodiousHarmonyEvents", remap = false)
public abstract class MixinCotsDebugSpam {

    @Redirect(
            method = "onLivingTick",
            at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"),
            require = 0
    )
    private static void coo$dropDebugSpam(PrintStream stream, String message) {
        if (!CoOConfig.covenantofthesevenSilenceDebugSpam) {
            stream.println(message);
        }
    }
}
