package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Tidal_Tentacle_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Tidal_Tentacle_Entity.class, remap = false)
public abstract class MixinCataclysmTidalTentacleTarget {

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/entity/projectile/Tidal_Tentacle_Entity;hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z"),
            require = 0
    )
    private boolean coo$distanceBeforeSight(Tidal_Tentacle_Entity self, Entity candidate, Operation<Boolean> original,
                                            @Local(name = "closestValid") Entity closestValid) {
        if (CoOConfig.cataclysmTentacleDistanceBeforeSight && closestValid != null) {
            Entity me = (Entity) (Object) this;
            if (!(me.distanceTo(candidate) < me.distanceTo(closestValid))) {
                return false;
            }
        }
        return original.call(self, candidate);
    }
}
