package com.misanthropy.collections_of_optimizations.mixin.simplyswords;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.sweenus.simplyswords.loot.PityLootContext", remap = false)
public abstract class MixinPityLootContext {

    @WrapOperation(
            method = "take",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;remove()V"),
            require = 0
    )
    private static void coo$keepEmptyDeque(ThreadLocal<?> local, Operation<Void> original) {
        if (!CoOConfig.simplyswordsReusePityLootDeque) {
            original.call(local);
        }
    }
}
