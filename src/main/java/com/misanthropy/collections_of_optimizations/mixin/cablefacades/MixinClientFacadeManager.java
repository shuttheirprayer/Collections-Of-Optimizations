package com.misanthropy.collections_of_optimizations.mixin.cablefacades;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CableFacadePresence;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.portingdeadmods.cable_facades.utils.ClientFacadeManager", remap = false)
public abstract class MixinClientFacadeManager {

    @Inject(method = "put", at = @At("HEAD"), require = 0)
    private static void coo$armOnPut(CallbackInfo ci) {
        CableFacadePresence.arm();
    }

    @Inject(method = "putAll", at = @At("HEAD"), require = 0)
    private static void coo$armOnPutAll(CallbackInfo ci) {
        CableFacadePresence.arm();
    }

    @Inject(method = "addDirectionalFacade(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"), require = 0)
    private static void coo$armOnDirectional(CallbackInfo ci) {
        CableFacadePresence.arm();
    }

    @Inject(method = "trackChunk", at = @At("HEAD"), require = 0)
    private static void coo$armOnTrackChunk(CallbackInfo ci) {
        CableFacadePresence.arm();
    }

    @Inject(method = "clear", at = @At("HEAD"), require = 0)
    private static void coo$disarmOnClear(CallbackInfo ci) {
        CableFacadePresence.disarm();
    }

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static Object coo$skipEmptyLookup(Map<Object, Object> map, Object key) {
        if (CoOConfig.cablefacadesSkipEmptyLookup && !CableFacadePresence.any()) {
            return null;
        }
        return map.get(key);
    }
}
