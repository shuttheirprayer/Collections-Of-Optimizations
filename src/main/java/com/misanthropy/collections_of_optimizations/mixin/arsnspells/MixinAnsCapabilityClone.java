package com.misanthropy.collections_of_optimizations.mixin.arsnspells;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.AnsCooldownCarry;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.otectus.arsnspells.data.ModCapabilityProvider", remap = false)
public abstract class MixinAnsCapabilityClone {

    @Inject(
            method = "onPlayerClone",
            at = @At("HEAD"),
            require = 0
    )
    private static void coo$carryCooldowns(PlayerEvent.Clone event, CallbackInfo ci) {
        if (!CoOConfig.arsnspellsCarryCooldownsThroughClone) {
            return;
        }
        AnsCooldownCarry.copy(event.getOriginal(), event.getEntity());
    }
}
