package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PartNameCache;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import strm.touchnmotion.TouchNMotionHook;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;

@Mixin(value = TouchNMotionHook.class, remap = false)
public abstract class MixinHookPartNames {

    @Redirect(method = "onAnimationEnd", at = @At(value = "INVOKE",
            target = "Ltraben/entity_model_features/models/parts/EMFModelPartRoot;getAllVanillaPartsByNameEMF()Ljava/util/Map;"))
    private Map<String, EMFModelPartVanilla> coo$rememberPartNames(EMFModelPartRoot root) {
        Map<String, EMFModelPartVanilla> parts = root.getAllVanillaPartsByNameEMF();
        return CoOConfig.touchnmotionPartNameCache ? PartNameCache.of(parts) : parts;
    }
}
