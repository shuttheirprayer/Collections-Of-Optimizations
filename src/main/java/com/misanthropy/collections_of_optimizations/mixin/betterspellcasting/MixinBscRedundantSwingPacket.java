package com.misanthropy.collections_of_optimizations.mixin.betterspellcasting;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.kayn.better_spellcasting.event.AttackSpellHandler", remap = false)
public abstract class MixinBscRedundantSwingPacket {

    @Inject(
            method = "onClientAttackEntity(Lnet/minecraftforge/event/entity/player/AttackEntityEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipRedundantSwingPacket(AttackEntityEvent event, CallbackInfo ci) {
        if (!CoOConfig.betterspellcastingSkipRedundantSwingPacket) {
            return;
        }
        ci.cancel();
    }
}
