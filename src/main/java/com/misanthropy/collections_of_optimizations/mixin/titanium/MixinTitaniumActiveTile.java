package com.misanthropy.collections_of_optimizations.mixin.titanium;

import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ActiveTile.class, remap = false)
public abstract class MixinTitaniumActiveTile {

    @WrapOperation(
            method = "getFacingDirection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            remap = true,
            require = 0
    )
    private BlockState coo$useCachedFacingState(Level level, BlockPos pos, Operation<BlockState> original) {
        if (!CoOConfig.titaniumCachedFacingState) {
            return original.call(level, pos);
        }
        BlockState cached = ((BlockEntity) (Object) this).getBlockState();
        return cached != null ? cached : original.call(level, pos);
    }
}
