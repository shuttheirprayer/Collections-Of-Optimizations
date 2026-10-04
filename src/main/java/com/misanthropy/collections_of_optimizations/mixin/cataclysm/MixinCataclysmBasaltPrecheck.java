package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.google.common.collect.ImmutableList;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BasaltColumnsFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasaltColumnsFeature.class, priority = 900)
public abstract class MixinCataclysmBasaltPrecheck {

    @Shadow
    @Final
    private static ImmutableList<Block> CANNOT_PLACE_ON;

    @Shadow
    private static boolean isAirOrLavaOcean(LevelAccessor level, int seaLevel, BlockPos pos) {
        throw new AssertionError();
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$vanillaRejectFirst(LevelAccessor level, int seaLevel, BlockPos.MutableBlockPos pos,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.cataclysmBasaltPrecheck) {
            return;
        }
        if (!isAirOrLavaOcean(level, seaLevel, pos)) {
            cir.setReturnValue(false);
            return;
        }
        BlockState below = level.getBlockState(pos.move(Direction.DOWN));
        pos.move(Direction.UP);
        if (below.isAir() || CANNOT_PLACE_ON.contains(below.getBlock())) {
            cir.setReturnValue(false);
        }
    }
}
