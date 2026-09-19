package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinFdBossesEntityTracking {

    @Inject(
            method = "startTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesTrackStart(EntityAccess entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingStart(entity, false);
    }

    @Inject(
            method = "stopTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$fdBossesTrackEnd(EntityAccess entity, CallbackInfo ci) {
        FdBossesPresence.onTrackingEnd(entity, false);
    }
}
