package com.misanthropy.collections_of_optimizations.mixin.soulsweapons;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.soulsweaponry.api.entitystats.EntityPosture;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.events.ModEvents;
import net.soulsweaponry.registry.AttributeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModEvents.class, remap = false)
public abstract class MixinModEventsIdlePosture {

    @Inject(method = "onLivingEntityTicks", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipIdleMob(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.soulsweaponsSkipIdleBuildupTick) {
            return;
        }
        LivingEntity entity = event.getEntity();
        int frostInterval = (int) ConfigConstructor.frost_reduction_interval;
        if (entity instanceof Player || frostInterval <= 0 || entity.tickCount % frostInterval == 0
                || (int) ConfigConstructor.max_bleed < 1 || (int) ConfigConstructor.max_frost_buildup < 1 || EntityPosture.BASE_POSTURE < 0) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        if (data.getInt("posture") > 0 || data.getInt("bleed") > 0 || data.getInt("frost_status") > 0) {
            return;
        }
        AttributeInstance bonus = entity.getAttribute(AttributeRegistry.BASE_POSTURE_INCREASE.get());
        if (bonus == null || bonus.getValue() >= 0.0D) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "onLivingEntityTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/soulsweaponry/api/entitystats/EntityPosture;getMaxPostureLoss(Lnet/minecraft/world/entity/LivingEntity;)I",
                    ordinal = 0
            ),
            require = 0
    )
    private static int coo$skipIdlePostureMax(LivingEntity entity, Operation<Integer> original, @Local(ordinal = 0) int posture) {
        if (CoOConfig.soulsweaponsSkipIdlePostureMax
                && posture <= 0
                && !(entity instanceof Player)
                && EntityPosture.BASE_POSTURE >= 0) {
            AttributeInstance bonus = entity.getAttribute(AttributeRegistry.BASE_POSTURE_INCREASE.get());
            if (bonus == null || bonus.getValue() >= 0.0D) {
                return Integer.MAX_VALUE;
            }
        }
        return original.call(entity);
    }
}
