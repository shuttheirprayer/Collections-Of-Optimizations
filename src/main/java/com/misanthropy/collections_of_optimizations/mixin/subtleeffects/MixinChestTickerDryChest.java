package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import einstein.subtle_effects.ticking.tickers.ChestBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlockEntityTicker.class)
public abstract class MixinChestTickerDryChest {

    @Shadow(remap = false)
    private int ticksSinceLastAnimation;

    @Shadow(remap = false)
    private int animationTicks;

    @WrapOperation(
            method = "positionedTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ChestBlock;isChestBlockedAt(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Z", ordinal = 0, remap = true),
            remap = false,
            require = 0
    )
    private boolean coo$skipDryChest(LevelAccessor level, BlockPos pos, Operation<Boolean> original) {
        if (CoOConfig.subtleeffectsDryChestTicker
                && this.animationTicks == 0
                && this.ticksSinceLastAnimation == 0
                && !ChestBlockEntityTicker.isUnderwater((Level) level, pos)) {
            return true;
        }
        return original.call(level, pos);
    }
}
