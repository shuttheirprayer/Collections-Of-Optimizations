package com.misanthropy.collections_of_optimizations.mixin.hexerei;

import com.misanthropy.collections_of_optimizations.core.LazyModelSwaps;
import net.joefoxe.hexerei.block.connected.ModelSwapper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@Mixin(value = ModelSwapper.class, remap = false)
public abstract class MixinHexereiLazyModelSwaps {

    @Inject(
            method = "swapModels(Ljava/util/Map;Lnet/minecraft/client/resources/model/ModelResourceLocation;Ljava/util/function/Function;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$deferSwap(Map<ResourceLocation, BakedModel> modelRegistry, ModelResourceLocation location,
                                      Function<BakedModel, ? extends BakedModel> factory, CallbackInfo ci) {
        if (LazyModelSwaps.defer(modelRegistry, location, factory)) {
            ci.cancel();
        }
    }
}
