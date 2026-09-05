package com.misanthropy.collections_of_optimizations.mixin.create;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.simibubi.create.foundation.block.BigOutlines;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BigOutlines.class, remap = false)
public abstract class MixinBigOutlinesPickRate {

    @Shadow
    static BlockHitResult result;

    @Unique
    private static long coo$lastPick;

    @Inject(method = "pick()V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$throttlePick(CallbackInfo ci) {
        int interval = CoOConfig.createBigOutlinePickInterval;
        if (interval <= 0) {
            return;
        }

        long now = System.nanoTime();
        if (now - coo$lastPick < interval * 1_000_000L) {
            BlockHitResult cached = result;
            if (cached != null) {
                Minecraft.getInstance().hitResult = cached;
            }
            ci.cancel();
            return;
        }

        coo$lastPick = now;
    }
}
