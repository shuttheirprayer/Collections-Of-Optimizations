package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.LongPredicate;

@Mixin(LevelTicks.class)
public abstract class MixinLevelTicksBlockedContainers {

    @WrapOperation(
            method = "sortContainersToTick",
            at = @At(value = "INVOKE", target = "Ljava/util/function/LongPredicate;test(J)Z")
    )
    private boolean coo$deferBlockedContainer(LongPredicate check, long chunk, Operation<Boolean> original,
                                              @Local Long2LongMap.Entry entry, @Local(argsOnly = true) long time) {
        boolean ticking = original.call(check, chunk);
        if (!ticking && CoOConfig.vanillaDeferBlockedTickContainers) {
            entry.setValue(time + 20L);
        }
        return ticking;
    }
}
