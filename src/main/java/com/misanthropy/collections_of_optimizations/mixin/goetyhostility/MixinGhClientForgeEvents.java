package com.misanthropy.collections_of_optimizations.mixin.goetyhostility;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModEntityFilter;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.ratrod.goetyhostility.client.event.GHClientForgeEvents", remap = false)
public abstract class MixinGhClientForgeEvents {

    @Inject(method = "onEntityTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$onlyHandleWildfire(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (CoOConfig.goetyhostilityLeanBossMusicTick && !ModEntityFilter.GOETY_HOSTILITY_WILDFIRE.matches(event.getEntity())) {
            ci.cancel();
        }
    }
}
