package com.misanthropy.collections_of_optimizations.mixin.covenantoftheseven;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.llenzzz.covenant_of_the_seven.events.ELItemModifierEvents", remap = false)
public abstract class MixinCotsCursedScrollPhase {

    @Inject(method = "cursedScrollAdditionalAttributeModifier", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cursedScrollEndPhaseOnly(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (CoOConfig.covenantofthesevenTickEndPhaseOnly && event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
