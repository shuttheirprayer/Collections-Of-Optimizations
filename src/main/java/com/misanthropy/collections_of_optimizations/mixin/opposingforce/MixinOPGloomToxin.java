package com.misanthropy.collections_of_optimizations.mixin.opposingforce;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.unusualmodding.opposing_force.effects.GloomToxin", remap = false)
public abstract class MixinOPGloomToxin {

    @Inject(method = "m_6742_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipClientEffectTick(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (!CoOConfig.opposingforceSkipClientEffectTicks || entity == null) {
            return;
        }
        if (entity.level().isClientSide) {
            ci.cancel();
        }
    }
}
