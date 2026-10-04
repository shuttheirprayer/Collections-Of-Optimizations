package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.animation.math.EMFMath;

import java.util.Map;

@Mixin(value = EMFMath.class, remap = false)
public abstract class MixinEMFAnimationEntityContext {

    @WrapOperation(
            method = "getAngerTime",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0
            ),
            require = 0
    )
    private static Object coo$dropZeroAngerEntry(Map<Object, Object> map,
                                                 Object key,
                                                 Object value,
                                                 Operation<Object> original) {
        if (!CoOConfig.emfDropZeroAngerEntries) {
            return original.call(map, key, value);
        }
        return map.remove(key);
    }
}
