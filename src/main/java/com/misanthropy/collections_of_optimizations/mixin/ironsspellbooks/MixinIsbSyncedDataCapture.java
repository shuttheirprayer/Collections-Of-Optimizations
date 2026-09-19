package com.misanthropy.collections_of_optimizations.mixin.ironsspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.IsbSyncedDataRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

@Pseudo
@Mixin(targets = "io.redspace.ironsspellbooks.player.ClientMagicData", remap = false)
public abstract class MixinIsbSyncedDataCapture {

    @WrapOperation(
            method = "handlePlayerSyncedData",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object coo$captureSyncedDataLookup(HashMap<Object, Object> lookup, Object key, Object value,
                                                      Operation<Object> original) {
        IsbSyncedDataRegistry.capture(lookup);
        return original.call(lookup, key, value);
    }
}
