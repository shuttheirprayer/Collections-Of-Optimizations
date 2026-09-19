package com.misanthropy.collections_of_optimizations.mixin.species;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class MixinSpeciesLivingEntityEffects {

    @Inject(
            method = "applySpeciesEffects(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$skipClientSpeciesEffectTick(CallbackInfo speciesCi, CallbackInfo ci) {
        if (CoOConfig.speciesSkipClientEffectTick && ((LivingEntity) (Object) this).level().isClientSide) {
            ci.cancel();
        }
    }
}
