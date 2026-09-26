package com.misanthropy.collections_of_optimizations.mixin.sliceanddice;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.SliceAndDiceWetAir;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Entity.class, priority = 1500, remap = false)
public abstract class MixinSadEntityWetAirExtinguish {

    @WrapOperation(
            method = "/.*\\$sliceanddice\\$baseTick$/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_8055_(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            remap = false,
            require = 0
    )
    private BlockState coo$skipWetAirLookup(Level level, BlockPos pos, Operation<BlockState> original) {
        if (SliceAndDiceWetAir.skipExtinguishCheck((Entity) (Object) this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return original.call(level, pos);
    }
}
