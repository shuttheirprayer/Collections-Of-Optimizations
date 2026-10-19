package com.misanthropy.collections_of_optimizations.mixin.modernfix;

import com.misanthropy.collections_of_optimizations.core.LazyModelSwaps;
import com.misanthropy.collections_of_optimizations.core.LazySwapHook;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.dynamicresources.DynamicBakedModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.BiFunction;

@Mixin(value = DynamicBakedModelProvider.class, remap = false)
public abstract class MixinDynamicBakedModelLazySwaps implements LazySwapHook {

    @Redirect(
            method = "get(Ljava/lang/Object;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            require = 0
    )
    private Object coo$resolveBeforeGet(Map<ResourceLocation, BakedModel> overrides, Object key, Object fallback) {
        LazyModelSwaps.resolve(key);
        return overrides.getOrDefault(key, (BakedModel) fallback);
    }

    @Inject(method = "put(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/BakedModel;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforePut(ResourceLocation key, BakedModel model, CallbackInfoReturnable<BakedModel> cir) {
        LazyModelSwaps.resolve(key);
    }

    @Inject(method = "remove(Ljava/lang/Object;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeRemove(Object key, CallbackInfoReturnable<BakedModel> cir) {
        LazyModelSwaps.resolve(key);
    }

    @Inject(method = "replace(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/BakedModel;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeReplace(ResourceLocation key, BakedModel model, CallbackInfoReturnable<BakedModel> cir) {
        LazyModelSwaps.resolve(key);
    }

    @Inject(method = "putAll(Ljava/util/Map;)V", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforePutAll(Map<? extends ResourceLocation, ? extends BakedModel> models, CallbackInfo ci) {
        LazyModelSwaps.resolveAll(models);
    }

    @Inject(method = "replaceAll(Ljava/util/function/BiFunction;)V", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeReplaceAll(BiFunction<? super ResourceLocation, ? super BakedModel, ? extends BakedModel> function, CallbackInfo ci) {
        LazyModelSwaps.resolveEverything();
    }

    @Inject(method = "containsValue(Ljava/lang/Object;)Z", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeContainsValue(Object value, CallbackInfoReturnable<Boolean> cir) {
        LazyModelSwaps.resolveEverything();
    }

    @Inject(method = "dumpStats()V", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeDump(CallbackInfo ci) {
        LazyModelSwaps.resolveEverything();
    }

    @Inject(method = "clear()V", at = @At("HEAD"), require = 0)
    private void coo$resolveBeforeClear(CallbackInfo ci) {
        LazyModelSwaps.resolveEverything();
    }
}
