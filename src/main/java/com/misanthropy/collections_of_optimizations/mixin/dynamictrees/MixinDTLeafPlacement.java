package com.misanthropy.collections_of_optimizations.mixin.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.treedata.TreePart;
import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DtLeafScratch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DynamicLeavesBlock.class, remap = false)
public abstract class MixinDTLeafPlacement {

    @Inject(
            method = "isLocationSuitableForNewLeaves(Lnet/minecraft/world/level/LevelAccessor;Lcom/ferreusveritas/dynamictrees/block/leaves/LeavesProperties;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$leanLeafPlacement(LevelAccessor level, LeavesProperties leavesProperties, BlockPos pos,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.dynamictreesLeanLeafPlacement) {
            return;
        }

        BlockState here = level.getBlockState(pos);
        Block block = here.getBlock();
        if (block instanceof DynamicLeavesBlock) {
            cir.setReturnValue(false);
            return;
        }

        BlockPos.MutableBlockPos below = DtLeafScratch.placementPos()
                .set(pos.getX(), pos.getY() - 1, pos.getZ());
        BlockState belowState = level.getBlockState(below);
        Block belowBlock = belowState.getBlock();

        if (!leavesProperties.canGrowOnGround()
                && (belowBlock instanceof LiquidBlock
                || belowState.canOcclude() && !TreeHelper.isBranch(belowState) && !(belowBlock instanceof LeavesBlock))) {
            cir.setReturnValue(false);
            return;
        }

        BlockState target = here;
        if (block instanceof DoublePlantBlock
                && here.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER
                && belowBlock instanceof DoublePlantBlock
                && belowState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
            if (block == Blocks.TALL_GRASS) {
                level.setBlock(below.immutable(), Blocks.GRASS.defaultBlockState(), 3);
            } else if (block == Blocks.LARGE_FERN) {
                level.setBlock(below.immutable(), Blocks.FERN.defaultBlockState(), 3);
            }
            level.removeBlock(pos, false);
            target = level.getBlockState(pos);
        }

        cir.setReturnValue((target.isAir() || target.canBeReplaced())
                && ((DynamicLeavesBlock) (Object) this).hasAdequateLight(here, level, leavesProperties, pos));
    }

    @Inject(
            method = "hasAdequateLight(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lcom/ferreusveritas/dynamictrees/block/leaves/LeavesProperties;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$leanLightCheck(BlockState state, LevelAccessor level, LeavesProperties leavesProperties, BlockPos pos,
                                    CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.dynamictreesLeanLeafPlacement) {
            return;
        }

        if (level.canSeeSky(pos)) {
            cir.setReturnValue(true);
            return;
        }

        int smother = leavesProperties.getSmotherLeavesMax();
        if (smother != 0) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            BlockPos.MutableBlockPos scan = DtLeafScratch.lightPos();

            BlockState belowState = level.getBlockState(scan.set(x, y - 1, z));
            TreePart belowPart = TreeHelper.getTreePart(belowState);
            if (belowPart == TreeHelper.NULL_TREE_PART || belowPart.getRadius(belowState) > 1) {
                boolean smothered = true;
                for (int i = 1; i <= smother; i++) {
                    if (!TreeHelper.isTreePart(level.getBlockState(scan.set(x, y + i, z)))) {
                        smothered = false;
                        break;
                    }
                }
                if (smothered) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        int required = TreeHelper.isLeaves(state)
                ? leavesProperties.getLightRequirement() - 2
                : leavesProperties.getLightRequirement();
        cir.setReturnValue(level.getBrightness(LightLayer.SKY, pos) >= required);
    }
}
