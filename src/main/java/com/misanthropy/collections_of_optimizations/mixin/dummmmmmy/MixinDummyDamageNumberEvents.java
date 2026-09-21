package com.misanthropy.collections_of_optimizations.mixin.dummmmmmy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DummyDamageNumbers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.mehvahdjukaar.dummmmmmy.common.ModEvents", remap = false)
public abstract class MixinDummyDamageNumberEvents {

    @Inject(
            method = "onEntityDamage(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$skipUnusedDamageNumber(LivingEntity target, float amount, DamageSource source, CallbackInfo ci) {
        if (!CoOConfig.dummmmmmySkipUnusedDamageNumbers) {
            return;
        }
        boolean attackerIsServerPlayer = source != null && source.getEntity() instanceof ServerPlayer;
        if (DummyDamageNumbers.skipDamageNumber(attackerIsServerPlayer)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onEntityHeal(Lnet/minecraft/world/entity/LivingEntity;F)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$skipUnusedHealNumber(LivingEntity target, float amount, CallbackInfo ci) {
        if (!CoOConfig.dummmmmmySkipUnusedDamageNumbers) {
            return;
        }
        if (DummyDamageNumbers.skipHealNumber(target instanceof ServerPlayer)) {
            ci.cancel();
        }
    }
}
