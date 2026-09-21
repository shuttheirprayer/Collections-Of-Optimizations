package com.misanthropy.collections_of_optimizations.mixin.toomanyglyphs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(targets = "io.github.derringersmods.toomanyglyphs.common.glyphs.EffectChaining", remap = false)
public abstract class MixinChainingBlockSearch {

    @Unique
    private static double coo$centerDistanceSqr(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
        return (double) dx * (double) dx + (double) dy * (double) dy + (double) dz * (double) dz;
    }

    @Inject(
            method = "lambda$onResolveBlock$1(Lnet/minecraft/core/BlockPos;DLjava/util/function/Predicate;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$leanChainNeighbourTest(BlockPos origin, double searchDistanceSqr, Predicate<BlockPos> isMatch, BlockPos candidate, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.toomanyglyphsLeanChainSearch) {
            return;
        }
        cir.setReturnValue(coo$centerDistanceSqr(origin, candidate) <= searchDistanceSqr && isMatch.test(candidate));
    }

    @Inject(
            method = "lambda$onResolveBlock$0(Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/core/BlockPos;)Ljava/lang/Double;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$leanChainDistanceAdjustment(BlockHitResult rayTraceResult, BlockPos pos, CallbackInfoReturnable<Double> cir) {
        if (!CoOConfig.toomanyglyphsLeanChainSearch) {
            return;
        }
        cir.setReturnValue(Math.sqrt(coo$centerDistanceSqr(pos, rayTraceResult.getBlockPos())) * 0.01);
    }
}
