package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.GeckoShare;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.core.keyframe.Keyframe;
import software.bernie.geckolib.core.molang.expressions.MolangValue;
import software.bernie.geckolib.loading.json.typeadapter.BakedAnimationsAdapter;

@Mixin(value = BakedAnimationsAdapter.class, remap = false)
public abstract class MixinBakedAnimationsAdapterShare {

    @ModifyExpressionValue(
            method = "buildKeyframeStack",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/core/molang/MolangParser;parseJson(Lcom/google/gson/JsonElement;)Lsoftware/bernie/geckolib/core/molang/expressions/MolangValue;"),
            require = 0
    )
    private MolangValue coo$shareValue(MolangValue made) {
        return CoOConfig.geckolibShareAnimationData ? GeckoShare.value(made) : made;
    }

    @ModifyExpressionValue(
            method = "buildKeyframeStack",
            at = @At(value = "NEW", target = "com/eliotlash/mclib/math/Constant"),
            require = 0
    )
    private Constant coo$shareConstant(Constant made) {
        return CoOConfig.geckolibShareAnimationData ? GeckoShare.constant(made) : made;
    }

    @ModifyExpressionValue(
            method = "buildKeyframeStack",
            at = @At(value = "NEW", target = "software/bernie/geckolib/core/keyframe/Keyframe"),
            require = 0
    )
    private Keyframe<IValue> coo$shareKeyframe(Keyframe<IValue> made) {
        return CoOConfig.geckolibShareAnimationData ? GeckoShare.keyframe(made) : made;
    }
}
