package com.misanthropy.collections_of_optimizations.mixin.manybows;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ManyBowsQuiverState;
import net.bandit.many_bows.compat.OriginsQuiverCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;

@Mixin(value = OriginsQuiverCompat.class, remap = false)
public abstract class MixinOriginsQuiverCompat {

    @Inject(method = "onTrackedStackShrunk", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipDeadQuiverTracking(ItemStack stack, CallbackInfo ci) {
        if (CoOConfig.manybowsSkipInactiveOriginsQuiver && !ManyBowsQuiverState.quiverPossible()) {
            ci.cancel();
        }
    }

    @Inject(method = "getInventoryPowers", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipDeadPowerLookup(Player player, CallbackInfoReturnable<Collection<?>> cir) {
        if (CoOConfig.manybowsSkipInactiveOriginsQuiver && !ManyBowsQuiverState.quiverPossible()) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}
