package com.misanthropy.collections_of_optimizations.mixin.alltheleaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "dev.uncandango.alltheleaks.events.common.GameBusEvent", remap = false)
public abstract class MixinAtlServerStopGc {

    @WrapOperation(
            method = "onClosedServer",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/uncandango/alltheleaks/feature/common/mods/minecraft/MemoryMonitor;runExplicitGc()Z"
            ),
            require = 0
    )
    private static boolean coo$skipServerStopGc(Operation<Boolean> original) {
        if (CoOConfig.alltheleaksSkipServerStopGc) {
            return false;
        }
        return original.call();
    }
}
