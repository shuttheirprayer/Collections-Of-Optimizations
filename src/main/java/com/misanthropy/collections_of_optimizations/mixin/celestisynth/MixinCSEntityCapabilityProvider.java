package com.misanthropy.collections_of_optimizations.mixin.celestisynth;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CelestisynthCapHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "org.thecelestialworkshop.celestisynth.common.capabilities.CSEntityCapabilityProvider", remap = false)
public abstract class MixinCSEntityCapabilityProvider {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$fastCsCap(LivingEntity entity, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (!CoOConfig.celestisynthFastCapabilityLookup || entity == null || entity.isRemoved()) {
            return;
        }
        if (!(entity instanceof CelestisynthCapHolder holder)) {
            return;
        }
        if (holder.coo$getCsCap() instanceof LazyOptional<?> cached && cached.isPresent()) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "get", at = @At("RETURN"), require = 0)
    private static void coo$rememberCsCap(LivingEntity entity, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (!CoOConfig.celestisynthFastCapabilityLookup || entity == null || entity.isRemoved()) {
            return;
        }
        if (entity instanceof CelestisynthCapHolder holder) {
            holder.coo$setCsCap(cir.getReturnValue());
        }
    }
}
