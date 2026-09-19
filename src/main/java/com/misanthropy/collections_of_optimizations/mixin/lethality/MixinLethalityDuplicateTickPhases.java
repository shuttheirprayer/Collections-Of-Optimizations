package com.misanthropy.collections_of_optimizations.mixin.lethality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "net.daphne.lethality.client.BladeModeEvents",
        "net.daphne.lethality.events.HFMeowrasamaEvents"
}, remap = false)
public abstract class MixinLethalityDuplicateTickPhases {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$singleTickPhase(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.lethalitySkipDuplicateTickPhases || event == null) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
