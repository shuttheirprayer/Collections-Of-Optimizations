package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.finderfeed.fdbosses.BossEvents", remap = false)
public abstract class MixinFdBossesArenaProtection {

    @Inject(
            method = "preventArenaDestruction(Lnet/minecraftforge/event/level/BlockEvent$BreakEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipArenaBreakScan(BlockEvent.BreakEvent event, CallbackInfo ci) {
        if (CoOConfig.fdbossesSkipArenaProtectionScan && FdBossesPresence.noServerSpawners()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "preventArenaDestruction(Lnet/minecraftforge/event/level/BlockEvent$EntityPlaceEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipArenaPlaceScan(BlockEvent.EntityPlaceEvent event, CallbackInfo ci) {
        if (CoOConfig.fdbossesSkipArenaProtectionScan && FdBossesPresence.noServerSpawners()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "preventArenaDestruction(Lnet/minecraftforge/event/level/ExplosionEvent$Detonate;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipArenaExplosionScan(ExplosionEvent.Detonate event, CallbackInfo ci) {
        if (CoOConfig.fdbossesSkipArenaProtectionScan && FdBossesPresence.noServerSpawners()) {
            ci.cancel();
        }
    }
}
