package com.misanthropy.collections_of_optimizations.mixin.tomeofwonders;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.platypushasnohat.tome_of_wonders.blocks.blockentity.WhirligigBlockEntity", remap = false)
public abstract class MixinTowWhirligigPower {

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/platypushasnohat/tome_of_wonders/blocks/WhirligigBlock;updatePower(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            require = 0
    )
    private static boolean coo$serverOnlyPower(BlockState state, Level level, BlockPos pos) {
        return !CoOConfig.tomeofwondersServerOnlyWhirligigPower || !level.isClientSide();
    }
}
