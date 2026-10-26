package com.misanthropy.collections_of_optimizations.mixin.enigmaticaddons;

import auviotre.enigmatic.addon.contents.items.ExtradimensionalScepter;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PersistentKeys;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExtradimensionalScepter.class, remap = false)
public abstract class MixinExtradimensionalScepter {

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipUnsetPersistentData(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.enigmaticaddonsSkipUnsetPersistentData) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof EntityPersistentDataAccessor access && PersistentKeys.lacks(access.coo$persistentData(), "ExtradimensionCounter")) {
            ci.cancel();
        }
    }
}
