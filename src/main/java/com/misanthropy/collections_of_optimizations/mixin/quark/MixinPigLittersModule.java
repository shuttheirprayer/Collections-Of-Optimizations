package com.misanthropy.collections_of_optimizations.mixin.quark;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PersistentKeys;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.content.tweaks.module.PigLittersModule;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;

@Mixin(value = PigLittersModule.class, remap = false)
public abstract class MixinPigLittersModule {

    @Inject(method = "onEntityUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipPigLitterTagChurn(ZLivingTick event, CallbackInfo ci) {
        if (!CoOConfig.quarkSkipPigLitterTagChurn) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof EntityPersistentDataAccessor access && PersistentKeys.lacks(access.coo$persistentData(), "quark:AteGoldenCarrot")) {
            ci.cancel();
        }
    }
}
