package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.WeakLeafSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.biome.Climate$RTree")
public abstract class MixinClimateRTreeWeakLast {

    @Shadow
    @Final
    @Mutable
    private ThreadLocal<?> lastResult;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void coo$weakLastResult(CallbackInfo ci) {
        if (CoOConfig.vanillaWeakBiomeSearchSlot && lastResult.getClass() == ThreadLocal.class) {
            lastResult = new WeakLeafSlot();
        }
    }
}
