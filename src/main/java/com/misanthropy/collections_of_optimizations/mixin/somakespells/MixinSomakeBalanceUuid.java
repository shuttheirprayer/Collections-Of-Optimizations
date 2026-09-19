package com.misanthropy.collections_of_optimizations.mixin.somakespells;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SomakeSpellsUuidCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Pseudo
@Mixin(targets = "com.somakespells.event.AttributeBalanceHandler", remap = false)
public abstract class MixinSomakeBalanceUuid {

    @Inject(method = "uuid", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$memoBalanceUuid(String key, CallbackInfoReturnable<UUID> cir) {
        if (!CoOConfig.somakespellsCacheBalanceUuids || key == null) {
            return;
        }
        cir.setReturnValue(SomakeSpellsUuidCache.get(key));
    }
}
