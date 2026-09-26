package com.misanthropy.collections_of_optimizations.mixin.createaddition;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.mrh0.createaddition.sound.CASoundScapes", remap = false)
public abstract class MixinCASoundScapes {

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"), require = 0)
    private static boolean coo$fixAmbientToggle(boolean enabled) {
        return CoOConfig.createadditionFixAmbientSoundToggle ? !enabled : enabled;
    }
}
