package com.misanthropy.collections_of_optimizations.mixin.radium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DeclaredMethodScan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.reflection.ReflectionUtil", remap = false)
public abstract class MixinRadiumMethodOverrideScan {

    @Inject(
            method = "hasMethodOverride(Ljava/lang/Class;Ljava/lang/Class;ZLjava/lang/String;[Ljava/lang/Class;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$scanWithoutThrowing(Class<?> clazz, Class<?> superclass, boolean fallbackResult, String methodName,
                                                Class<?>[] methodArgs, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.radiumNoThrowMethodOverrideScan) {
            return;
        }
        int result = DeclaredMethodScan.hasOverride(clazz, superclass, methodName, methodArgs);
        if (result != DeclaredMethodScan.UNKNOWN) {
            cir.setReturnValue(result == 1);
        }
    }
}
