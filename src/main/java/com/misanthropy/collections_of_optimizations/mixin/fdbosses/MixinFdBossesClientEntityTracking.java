package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$EntityCallbacks")
public abstract class MixinFdBossesClientEntityTracking {

    @Inject(
            method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesClientTrackStart(Entity entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingStart(entity, true);
    }

    @Inject(
            method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesClientTrackEnd(Entity entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingEnd(entity, true);
    }
}
