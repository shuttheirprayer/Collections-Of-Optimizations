package com.misanthropy.collections_of_optimizations.mixin.zeta;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClusterBoundsFirst;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.zeta.world.generator.multichunk.ClusterBasedGenerator;
import org.violetmoon.zeta.world.generator.multichunk.ClusterShape;

import java.util.function.Consumer;

@Mixin(value = ClusterBasedGenerator.class, remap = false)
public abstract class MixinClusterBasedGeneratorBoundsFirst {

    @WrapOperation(
            method = "generateChunkPart",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/violetmoon/zeta/world/generator/multichunk/ClusterBasedGenerator;forEachChunkBlock(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;IILjava/util/function/Consumer;)V"
            ),
            require = 0
    )
    private void coo$skipUnreachedChunk(ClusterBasedGenerator generator,
                                        LevelReader level,
                                        BlockPos chunkCorner,
                                        int minY,
                                        int maxY,
                                        Consumer<BlockPos> action,
                                        Operation<Void> original,
                                        @Local ClusterShape shape,
                                        @Local ClusterBasedGenerator.IGenerationContext context) {
        if (CoOConfig.zetaClusterBoundsFirst && ClusterBoundsFirst.missesChunk(generator, context, shape, chunkCorner)) {
            return;
        }
        original.call(generator, level, chunkCorner, minY, maxY, action);
    }

    @WrapOperation(
            method = "lambda$generateChunkPart$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/violetmoon/zeta/world/generator/multichunk/ClusterBasedGenerator$IGenerationContext;canPlaceAt(Lnet/minecraft/core/BlockPos;)Z"
            ),
            require = 0
    )
    private static boolean coo$boundsFirst(ClusterBasedGenerator.IGenerationContext context,
                                           BlockPos pos,
                                           Operation<Boolean> original,
                                           @Local(argsOnly = true) ClusterShape shape) {
        if (CoOConfig.zetaClusterBoundsFirst && ClusterBoundsFirst.skips(context, shape, pos)) {
            return false;
        }
        return original.call(context, pos);
    }
}
