package com.misanthropy.collections_of_optimizations.mixin.covenantoftheseven;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.llenzzz.covenant_of_the_seven.events.item.curse.BlasphemyEvents", remap = false)
public abstract class MixinCotsBlasphemyPhase {

    @Inject(method = "blasphemyAttributeModifier", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$blasphemyEndPhaseOnly(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (CoOConfig.covenantofthesevenTickEndPhaseOnly && event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
