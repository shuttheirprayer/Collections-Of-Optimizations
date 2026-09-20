package com.misanthropy.collections_of_optimizations.mixin.corpse;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import de.maxhenkel.corpse.events.DeathEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DeathEvents.class, remap = false)
public abstract class MixinCorpseDeathSweep {

    @Unique
    private static long coo$lastSweep;

    @WrapWithCondition(
            method = "deleteOldDeaths(Lnet/minecraft/server/level/ServerLevel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Thread;start()V"
            ),
            require = 0
    )
    private static boolean coo$throttleDeathSweep(Thread thread) {
        int interval = CoOConfig.corpseDeathSweepInterval;
        if (interval <= 0) {
            return true;
        }
        long now = System.currentTimeMillis();
        long last = coo$lastSweep;
        if (last != 0L && now - last < interval * 1000L) {
            return false;
        }
        coo$lastSweep = now;
        return true;
    }
}
