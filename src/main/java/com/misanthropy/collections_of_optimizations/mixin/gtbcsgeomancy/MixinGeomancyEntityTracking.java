package com.misanthropy.collections_of_optimizations.mixin.gtbcsgeomancy;

import com.misanthropy.collections_of_optimizations.core.GeomancyGrandmasterTracker;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinGeomancyEntityTracking {

    @Inject(
            method = "startTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$geomancyTrackGrandmasterStart(EntityAccess entity, CallbackInfo ci) {
        GeomancyGrandmasterTracker.onTrackingStart(entity);
    }

    @Inject(
            method = "stopTracking",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$geomancyTrackGrandmasterEnd(EntityAccess entity, CallbackInfo ci) {
        GeomancyGrandmasterTracker.onTrackingEnd(entity);
    }
}
