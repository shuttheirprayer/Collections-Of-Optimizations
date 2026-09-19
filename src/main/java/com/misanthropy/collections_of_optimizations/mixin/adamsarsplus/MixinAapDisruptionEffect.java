package com.misanthropy.collections_of_optimizations.mixin.adamsarsplus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.AdamsDisruptionPurge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.adamsmods.adamsarsplus.entities.effects.DisruptionEffect", remap = false)
public abstract class MixinAapDisruptionEffect {

    @Inject(
            method = "m_6742_(Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$leanBeneficialPurge(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (!CoOConfig.adamsarsplusLeanDisruptionPurge || entity == null) {
            return;
        }
        ci.cancel();
        Level level = entity.level();
        if (level == null || level.isClientSide()) {
            return;
        }
        AdamsDisruptionPurge.expireBeneficial(entity);
    }
}
