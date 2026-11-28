package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevelIdleTick {

    @WrapWithCondition(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")
    )
    private boolean coo$tickUnlessIdle(Entity entity) {
        return !((IdleEntities.State) entity).coo$skipTick();
    }
}
