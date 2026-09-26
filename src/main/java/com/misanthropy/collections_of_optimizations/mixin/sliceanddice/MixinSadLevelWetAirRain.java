package com.misanthropy.collections_of_optimizations.mixin.sliceanddice;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.SliceAndDiceWetAir;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Level.class, priority = 1500, remap = false)
public abstract class MixinSadLevelWetAirRain {

    @WrapOperation(
            method = "/isRainingAt$/",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/possible_triangle/sliceanddice/block/sprinkler/WetAir;check(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"
            ),
            remap = false,
            require = 0
    )
    private boolean coo$skipWetAirScan(Level level, BlockPos pos, Operation<Boolean> original) {
        if (SliceAndDiceWetAir.skipRainCheck()) {
            return false;
        }
        return original.call(level, pos);
    }
}
