package com.misanthropy.collections_of_optimizations.mixin.dynamictrees;

import com.ferreusveritas.dynamictrees.block.branch.BasicBranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {BasicBranchBlock.class, BasicRootsBlock.class}, remap = false)
public abstract class MixinDTBoundedRapidRot {

    @ModifyExpressionValue(
            method = "checkForRot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/ferreusveritas/dynamictrees/tree/species/Species;rot(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;IIILnet/minecraft/util/RandomSource;ZZ)Z"
            ),
            require = 0
    )
    private boolean coo$rotOnlyIfGone(boolean original,
                                      @Local(argsOnly = true) LevelAccessor level,
                                      @Local(argsOnly = true) BlockPos pos) {
        if (!original || !CoOConfig.dynamictreesBoundedRapidRot) {
            return original;
        }
        return level.getBlockState(pos).getBlock() != (Object) this;
    }
}
