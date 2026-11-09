package com.misanthropy.collections_of_optimizations.mixin.roacw;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.kamaarion.roacw.client.firevisuals.FireEffectSyncHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(value = FireEffectSyncHandler.class, remap = false)
public abstract class MixinRoacwFireEffectSync {

    @Shadow @Final
    private static Map<Integer, Set<ResourceLocation>> LAST_SYNCED;

    @Inject(method = "onLivingTick", at = @At("HEAD"), cancellable = true)
    private static void coo$skipEntitiesWithoutEffects(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.roacwSkipIdleFireSync) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.getActiveEffectsMap().isEmpty() && (LAST_SYNCED.isEmpty() || !LAST_SYNCED.containsKey(entity.getId()))) {
            ci.cancel();
        }
    }
}
