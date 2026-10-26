package com.misanthropy.collections_of_optimizations.mixin.transmog;

import com.hidoni.transmog.TransmogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinTransmogPlayerAttack {

    @Inject(method = "attack", at = @At("HEAD"))
    private void coo$startPvP(Entity target, CallbackInfo ci) {
        if (((Player) (Object) this).isLocalPlayer() && target instanceof Player) {
            TransmogUtils.startPvP();
        }
    }
}
