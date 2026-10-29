package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ArmorPathCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

@Mixin(value = ItemArmorGeoLayer.class, remap = false)
public abstract class MixinGeoArmorResourcePath {

    @WrapOperation(
            method = "getVanillaArmorResource",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            require = 0
    )
    private String coo$cachedArmorPath(String format, Object[] args, Operation<String> original) {
        if (!CoOConfig.forgeArmorTexturePathCache) {
            return original.call(format, args);
        }
        String cached = ArmorPathCache.get(null, format, args);
        if (cached != null) {
            return cached;
        }
        return ArmorPathCache.put(null, format, args, original.call(format, args));
    }
}
