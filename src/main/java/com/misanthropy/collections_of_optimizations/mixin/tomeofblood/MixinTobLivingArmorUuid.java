package com.misanthropy.collections_of_optimizations.mixin.tomeofblood;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TierAttributeUuidCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Pseudo
@Mixin(targets = "com.mystchonky.tomeofblood.common.items.LivingMageArmorItem", remap = false)
public abstract class MixinTobLivingArmorUuid {

    @WrapOperation(
            method = "lambda$getAttributeModifiers$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/UUID;nameUUIDFromBytes([B)Ljava/util/UUID;"
            ),
            require = 0
    )
    private static UUID coo$memoiseLivingUpgradeUuid(byte[] name, Operation<UUID> original) {
        if (!CoOConfig.tomeofbloodCacheLivingUpgradeUuids) {
            return original.call(name);
        }
        UUID cached = TierAttributeUuidCache.lookup(name);
        if (cached != null) {
            return cached;
        }
        UUID computed = original.call(name);
        TierAttributeUuidCache.store(name, computed);
        return computed;
    }
}
