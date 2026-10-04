package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.Cataclysm;
import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinCataclysmSandstormSoundCleanup {

    @Inject(method = "onClientRemoval", at = @At("HEAD"), require = 0)
    private void coo$clearSandstormSound(CallbackInfo ci) {
        if (CoOConfig.cataclysmSandstormSoundCleanup && (Object) this instanceof Sandstorm_Entity) {
            Cataclysm.PROXY.clearSoundCacheFor((Entity) (Object) this);
        }
    }
}
