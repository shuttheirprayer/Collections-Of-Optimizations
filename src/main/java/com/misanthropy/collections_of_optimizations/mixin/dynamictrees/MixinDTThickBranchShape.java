package com.misanthropy.collections_of_optimizations.mixin.dynamictrees;

import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DtLeafScratch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ThickBranchBlock.class, remap = false)
public abstract class MixinDTThickBranchShape {

    @Inject(
            method = "m_5940_",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$cacheThickTrunkShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context,
                                          CallbackInfoReturnable<VoxelShape> cir) {
        if (!CoOConfig.dynamictreesCacheThickTrunkShape) {
            return;
        }

        VoxelShape shape = DtLeafScratch.thickTrunkShape(((ThickBranchBlock) (Object) this).getRadius(state));
        if (shape != null) {
            cir.setReturnValue(shape);
        }
    }
}
