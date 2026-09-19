package com.misanthropy.collections_of_optimizations.mixin.l2complements;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2ComplementsTags;
import dev.xkmc.l2complements.content.feature.EntityFeature;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.xkmc.l2complements.events.MagicEventHandler", remap = false)
public abstract class MixinL2CAttackFeatureChecks {

    @WrapOperation(
            method = "onLivingAttack(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xkmc/l2complements/content/feature/EntityFeature;test(Lnet/minecraft/world/entity/LivingEntity;)Z"
            ),
            require = 0
    )
    private static boolean coo$skipUnreachableFeatureChecks(
            EntityFeature feature,
            LivingEntity entity,
            Operation<Boolean> original,
            @Local(argsOnly = true) LivingAttackEvent event) {
        if (!CoOConfig.l2complementsLeanAttackChecks || event == null || entity == null) {
            return original.call(feature, entity);
        }

        DamageSource source = event.getSource();
        if (source == null) {
            return original.call(feature, entity);
        }

        if (feature == EntityFeature.OWNER_PROTECTION) {
            Entity attacker = source.getEntity();
            if (!(attacker instanceof OwnableEntity owned) || owned.getOwner() != entity) {
                return false;
            }
        } else if (feature == EntityFeature.ENVIRONMENTAL_REJECT) {
            if (source.getEntity() != null) {
                return false;
            }
        } else if (feature == EntityFeature.MAGIC_REJECT) {
            if (!source.is(L2ComplementsTags.IS_MAGIC)) {
                return false;
            }
        } else if (feature == EntityFeature.PROJECTILE_REJECT) {
            if (!source.is(DamageTypeTags.IS_PROJECTILE)) {
                return false;
            }
        } else if (feature == EntityFeature.FIRE_REJECT) {
            if (!source.is(DamageTypeTags.IS_FIRE)) {
                return false;
            }
        } else if (feature == EntityFeature.EXPLOSION_REJECT) {
            if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                return false;
            }
        }

        return original.call(feature, entity);
    }
}
