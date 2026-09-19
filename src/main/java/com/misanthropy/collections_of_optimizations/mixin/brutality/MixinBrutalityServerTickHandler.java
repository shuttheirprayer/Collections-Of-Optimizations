package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.goo.brutality.event.forge.ServerTickHandler", remap = false)
public abstract class MixinBrutalityServerTickHandler {

    @Inject(
            method = "onServerTick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$countOnePhaseOnly(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.brutalityFixDoubleTickCounters || event == null) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
