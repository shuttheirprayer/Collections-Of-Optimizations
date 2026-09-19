package com.misanthropy.collections_of_optimizations.mixin.bmd;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.cerbon.bosses_of_mass_destruction.event.ForgeEvents", remap = false)
public abstract class MixinBmdPlayerTickPhase {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$singlePhase(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.bmdSinglePlayerTickPhase || event == null) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
