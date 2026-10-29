package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ArmorPathCache;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Locale;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinArmorResourcePath {

    @WrapOperation(
            method = "getArmorResource",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            remap = false,
            require = 0
    )
    private String coo$cachedArmorPath(Locale locale, String format, Object[] args, Operation<String> original) {
        if (!CoOConfig.forgeArmorTexturePathCache) {
            return original.call(locale, format, args);
        }
        String cached = ArmorPathCache.get(locale, format, args);
        if (cached != null) {
            return cached;
        }
        return ArmorPathCache.put(locale, format, args, original.call(locale, format, args));
    }
}
