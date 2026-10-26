package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtDevourerIndex;
import com.misanthropy.collections_of_optimizations.core.FtEntityState;
import com.misanthropy.collections_of_optimizations.core.PersistentKeys;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.kayn.fallen_traits.events.FTEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FTEvents.class, remap = false)
public abstract class MixinFTEvents {

    @Inject(method = "onHurt", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnusedStreakReset(LivingHurtEvent event, CallbackInfo ci) {
        if (CoOConfig.fallentraitsSkipUnusedStreakReset
                && !(event.getEntity() instanceof FtEntityState state && state.coo$ftStreakUser())) {
            ci.cancel();
        }
    }

    @Inject(method = "onCloneTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnsetPersistentDataTick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (CoOConfig.fallentraitsSkipUnsetPersistentData && coo$lacks(event.getEntity(), "fallen_traits_is_clone")) {
            ci.cancel();
        }
    }

    @Inject(method = "onLivingTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnsetWrathTick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (CoOConfig.fallentraitsSkipUnsetPersistentData && coo$lacks(event.getEntity(), "ft.wrath_disabled_traits")) {
            ci.cancel();
        }
    }

    @Inject(method = "onCloneDamaged", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnsetPersistentDataHurt(LivingHurtEvent event, CallbackInfo ci) {
        if (CoOConfig.fallentraitsSkipUnsetPersistentData && coo$lacks(event.getEntity(), "fallen_traits_is_clone")) {
            ci.cancel();
        }
    }

    @Inject(method = "onDevourerHeal", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipHealWithoutDevourer(LivingHealEvent event, CallbackInfo ci) {
        if (!CoOConfig.fallentraitsIndexDevourers) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide() || event.getAmount() <= 0.0F) {
            return;
        }
        if (!FtDevourerIndex.mayAbsorb(target)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean coo$lacks(LivingEntity entity, String key) {
        return entity instanceof EntityPersistentDataAccessor access && PersistentKeys.lacks(access.coo$persistentData(), key);
    }
}
