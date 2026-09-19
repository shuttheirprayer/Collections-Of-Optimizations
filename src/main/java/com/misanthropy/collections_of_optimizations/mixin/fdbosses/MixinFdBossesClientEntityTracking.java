package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TransientEntitySectionManager.class)
public abstract class MixinFdBossesClientEntityTracking {

    @Inject(
            method = "startTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesClientTrackStart(EntityAccess entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingStart(entity, true);
    }

    @Inject(
            method = "stopTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesClientTrackEnd(EntityAccess entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingEnd(entity, true);
    }
}
