package com.misanthropy.collections_of_optimizations.mixin.xaerolib;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.XaeroColorCache;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.lib.client.level.block.BlockTextureColorUtils;

@Mixin(value = BlockTextureColorUtils.class, remap = false)
public abstract class MixinBlockTextureColorCache {

    @Inject(method = "requestCachedColoursClear", at = @At("HEAD"))
    private void coo$clearColors(CallbackInfo ci) {
        XaeroColorCache.reset();
    }

    @Inject(method = "getUncachedTextureColor", at = @At("HEAD"), cancellable = true)
    private void coo$cachedColor(ResourceLocation identifier, boolean exceptionOnBlack, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.xaerolibPersistBlockColors) {
            Integer color = XaeroColorCache.get(identifier, exceptionOnBlack);
            if (color != null) {
                cir.setReturnValue(color);
            }
        }
    }

    @Inject(method = "getUncachedTextureColor", at = @At("RETURN"))
    private void coo$storeColor(ResourceLocation identifier, boolean exceptionOnBlack, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.xaerolibPersistBlockColors) {
            XaeroColorCache.put(identifier, exceptionOnBlack, cir.getReturnValueI());
        }
    }
}
