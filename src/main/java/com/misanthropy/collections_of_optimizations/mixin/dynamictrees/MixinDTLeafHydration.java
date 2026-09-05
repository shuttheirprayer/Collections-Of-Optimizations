package com.misanthropy.collections_of_optimizations.mixin.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cell.Cell;
import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DtLeafScratch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DynamicLeavesBlock.class, remap = false)
public abstract class MixinDTLeafHydration {

    @Inject(
            method = "getHydrationLevelFromNeighbors(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lcom/ferreusveritas/dynamictrees/block/leaves/LeavesProperties;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$leanHydration(LevelAccessor level, BlockPos pos, LeavesProperties leavesProperties,
                                   CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.dynamictreesLeanLeafHydration) {
            return;
        }

        Cell[] cells = DtLeafScratch.hydrationCells();
        BlockPos.MutableBlockPos probe = DtLeafScratch.hydrationPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for (int i = 0; i < 6; i++) {
            Direction dir = DtLeafScratch.DIRECTIONS[i];
            probe.set(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
            BlockState state = level.getBlockState(probe);
            cells[i] = TreeHelper.getTreePart(state).getHydrationCell(level, probe, state, dir, leavesProperties);
        }

        cir.setReturnValue(leavesProperties.getCellKit().getCellSolver().solve(cells));
    }
}
