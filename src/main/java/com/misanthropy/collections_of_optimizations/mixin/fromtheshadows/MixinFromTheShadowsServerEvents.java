package com.misanthropy.collections_of_optimizations.mixin.fromtheshadows;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FromTheShadowsMask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.sonmok14.fromtheshadows.server.utils.event.ServerEvents", remap = false)
public abstract class MixinFromTheShadowsServerEvents {

    @Inject(method = "onLivingUpdateEvent", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipWithoutMask(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.fromtheshadowsSkipUnmaskedTick) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity != null && !FromTheShadowsMask.mayWearMask(entity)) {
            ci.cancel();
        }
    }
}
