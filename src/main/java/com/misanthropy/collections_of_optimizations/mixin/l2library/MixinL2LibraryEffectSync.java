package com.misanthropy.collections_of_optimizations.mixin.l2library;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2LibraryTrackedEffects;
import dev.xkmc.l2library.init.events.EffectSyncEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EffectSyncEvents.class, remap = false)
public abstract class MixinL2LibraryEffectSync {

    @Inject(
            method = "onPlayerStopTracking(Lnet/minecraftforge/event/entity/player/PlayerEvent$StopTracking;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipStopTrackingBroadcast(PlayerEvent.StopTracking event, CallbackInfo ci) {
        if (CoOConfig.l2libraryFixStopTrackingSync) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tracked(Lnet/minecraft/world/effect/MobEffect;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$cachedTracked(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.l2libraryCacheTrackedEffects || effect == null) {
            return;
        }
        Boolean cached = L2LibraryTrackedEffects.get(effect, EffectSyncEvents.TRACKED.size());
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "tracked(Lnet/minecraft/world/effect/MobEffect;)Z",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeTracked(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.l2libraryCacheTrackedEffects || effect == null) {
            return;
        }
        L2LibraryTrackedEffects.put(effect, EffectSyncEvents.TRACKED.size(), cir.getReturnValueZ());
    }
}
