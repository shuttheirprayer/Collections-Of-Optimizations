package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SubtleEffectsTickerRange;
import einstein.subtle_effects.ticking.tickers.entity.EntityTickerManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityTickerManager.class, remap = false)
public abstract class MixinEntityTickerManagerRange {

    @WrapOperation(
            method = "updateTickersForEntity",
            at = @At(value = "INVOKE", target = "Leinstein/subtle_effects/ticking/tickers/entity/EntityTickerManager;isEntityInRange(Lnet/minecraft/world/entity/Entity;I)Z"),
            require = 0
    )
    private static boolean coo$oneRangeCheck(Entity entity, int range, Operation<Boolean> original) {
        if (!CoOConfig.subtleeffectsOneRangeCheck) {
            return original.call(entity, range);
        }
        return SubtleEffectsTickerRange.inRange(entity, range);
    }
}
