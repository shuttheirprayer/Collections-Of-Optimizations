package com.misanthropy.collections_of_optimizations.mixin.fluidium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FluidiumClaimCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.kall.fluidium.common.integration.ClaimManager", remap = false)
public abstract class MixinFluidiumClaimLookup {

    @Inject(
            method = "isClaimed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0)
    private static void coo$answerClaimFromCache(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.fluidiumCacheClaimLookups || !FluidiumClaimCache.usable(level)) {
            return;
        }
        int cached = FluidiumClaimCache.lookup(level, pos);
        if (cached != FluidiumClaimCache.MISS) {
            cir.setReturnValue(cached != 0);
        }
    }

    @Inject(
            method = "isClaimed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN"),
            require = 0)
    private static void coo$rememberClaim(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.fluidiumCacheClaimLookups || !FluidiumClaimCache.usable(level)) {
            return;
        }
        FluidiumClaimCache.store(level, pos, cir.getReturnValueZ());
    }
}
