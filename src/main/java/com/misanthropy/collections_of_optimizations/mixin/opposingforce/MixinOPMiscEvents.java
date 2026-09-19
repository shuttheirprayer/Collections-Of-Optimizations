package com.misanthropy.collections_of_optimizations.mixin.opposingforce;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.OpposingForceStealthCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.unusualmodding.opposing_force.events.MiscEvents", remap = false)
public abstract class MixinOPMiscEvents {

    @Inject(method = "onLivingVisibility", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cacheStealthScan(LivingEvent.LivingVisibilityEvent event, CallbackInfo ci) {
        if (!CoOConfig.opposingforceCacheStealthVisibility || event == null) {
            return;
        }
        ci.cancel();
        if (event.getLookingEntity() == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        double stealth = OpposingForceStealthCache.stealth(entity);
        if (stealth > 0.0D) {
            event.modifyVisibility(Math.max(1.0D - stealth, 0.0D));
        }
    }
}
