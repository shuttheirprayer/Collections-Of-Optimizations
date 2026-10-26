package com.misanthropy.collections_of_optimizations.mixin.industrialforegoing;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PersistentKeys;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.buuz135.industrial.module.ModuleMisc", remap = false)
public abstract class MixinModuleMisc {

    @Inject(method = "lambda$generateFeatures$21", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipStasisTagChurn(LivingEvent.LivingTickEvent event,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.industrialforegoingSkipStasisTagChurn) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        if (entity instanceof EntityPersistentDataAccessor access && PersistentKeys.lacks(access.coo$persistentData(), "StasisChamberTime")) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
