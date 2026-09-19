package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = CuriosImplMixinHooks.class, remap = false)
public abstract class MixinCuriosUuidCache {

    @Mutable
    @Shadow
    @Final
    private static Map<String, UUID> UUIDS;

    @Inject(method = "<clinit>", at = @At("TAIL"), require = 0)
    private static void coo$useConcurrentUuidCache(CallbackInfo ci) {
        if (!CoOConfig.curiosThreadSafeCaches) {
            return;
        }
        Map<String, UUID> current = UUIDS;
        UUIDS = current == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(current);
    }
}
