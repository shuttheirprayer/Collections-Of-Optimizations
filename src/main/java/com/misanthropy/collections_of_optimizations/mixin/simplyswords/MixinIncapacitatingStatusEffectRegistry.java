package com.misanthropy.collections_of_optimizations.mixin.simplyswords;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Pseudo
@Mixin(targets = "net.sweenus.simplyswords.api.IncapacitatingStatusEffectRegistry", remap = false)
public abstract class MixinIncapacitatingStatusEffectRegistry {

    @Shadow
    @Final
    private static Set<ResourceLocation> EFFECTS;

    @Inject(
            method = "isIncapacitated(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$scanWithoutStream(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.simplyswordsFastIncapacitatedCheck) {
            return;
        }
        if (entity == null || EFFECTS.isEmpty()) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        Map<MobEffect, MobEffectInstance> active = entity.getActiveEffectsMap();
        if (active.isEmpty()) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        for (MobEffect effect : active.keySet()) {
            if (EFFECTS.contains(BuiltInRegistries.MOB_EFFECT.getKey(effect))) {
                cir.setReturnValue(Boolean.TRUE);
                return;
            }
        }
        cir.setReturnValue(Boolean.FALSE);
    }
}
