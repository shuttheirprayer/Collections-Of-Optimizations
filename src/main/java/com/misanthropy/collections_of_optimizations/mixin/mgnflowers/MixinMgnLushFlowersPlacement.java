package com.misanthropy.collections_of_optimizations.mixin.mgnflowers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "io.github.magishanpixel.mgnflowers.worldgen.features.LushFlowersFeature", remap = false)
public abstract class MixinMgnLushFlowersPlacement {

    @WrapOperation(
            method = "m_142674_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;m_60710_(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"
            ),
            require = 0
    )
    private boolean coo$onlyInOpenSpace(BlockState flower, LevelReader level, BlockPos origin,
                                        Operation<Boolean> original) {
        if (!original.call(flower, level, origin)) {
            return false;
        }
        if (!CoOConfig.mgnflowersKeepLushFlowersInOpenAir) {
            return true;
        }
        BlockState target = level.getBlockState(origin);
        return target.isAir() || target.is(Blocks.GRASS) || target.is(Blocks.MOSS_CARPET);
    }

    @WrapOperation(
            method = "m_142674_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;m_216332_(II)I",
                    ordinal = 0
            ),
            require = 0
    )
    private int coo$stopBelowCeiling(RandomSource random, int min, int max, Operation<Integer> original,
                                     @Local(argsOnly = true) FeaturePlaceContext<?> context) {
        int height = original.call(random, min, max);
        if (!CoOConfig.mgnflowersKeepLushFlowersInOpenAir) {
            return height;
        }
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos cursor = context.origin().mutable();
        int openAbove = 0;
        while (openAbove < height && level.getBlockState(cursor.move(Direction.UP)).isAir()) {
            openAbove++;
        }
        return openAbove;
    }
}
