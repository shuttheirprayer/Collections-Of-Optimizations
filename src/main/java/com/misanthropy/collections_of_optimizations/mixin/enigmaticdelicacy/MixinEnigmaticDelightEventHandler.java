package com.misanthropy.collections_of_optimizations.mixin.enigmaticdelicacy;

import auviotre.enigmatic.delicacy.handlers.EnigmaticDelightEventHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PersistentKeys;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EnigmaticDelightEventHandler.class, remap = false)
public abstract class MixinEnigmaticDelightEventHandler {

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipUnsetPersistentData(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.enigmaticdelicacySkipUnsetPersistentData) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof EntityPersistentDataAccessor access && PersistentKeys.lacks(access.coo$persistentData(), "Slicing")) {
            ci.cancel();
        }
    }
}
