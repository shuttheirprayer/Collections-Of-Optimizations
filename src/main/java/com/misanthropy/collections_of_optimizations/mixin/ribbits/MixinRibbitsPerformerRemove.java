package com.misanthropy.collections_of_optimizations.mixin.ribbits;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Pseudo
@Mixin(targets = "com.yungnickyoung.minecraft.ribbits.player.PlayerInstrumentTracker", remap = false)
public abstract class MixinRibbitsPerformerRemove {

    @WrapOperation(
            method = "removePerformer",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/ConcurrentHashMap;get(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object coo$guardAudienceLookup(ConcurrentHashMap<?, ?> map, Object key, Operation<Object> original) {
        Object audience = original.call(map, key);
        if (audience == null && CoOConfig.ribbitsFixPerformerCrash) {
            return Collections.emptySet();
        }
        return audience;
    }
}
