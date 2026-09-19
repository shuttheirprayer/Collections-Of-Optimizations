package com.misanthropy.collections_of_optimizations.mixin.textanimator;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TextAnimatorTagFilter;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.textanimator.duck.TAStyle;
import snownee.textanimator.util.CommonProxy;

@Mixin(value = CommonProxy.class, remap = false)
public abstract class MixinTextAnimatorCommonProxy {

    @ModifyExpressionValue(
            method = "iterateFormatted",
            at = @At(
                    value = "INVOKE",
                    target = "Lsnownee/textanimator/util/CommonProxy;isPhysicalClient()Z"
            ),
            require = 0
    )
    private static boolean coo$skipIdleTypewriterProbe(boolean original, @Local(argsOnly = true, ordinal = 0) Style style) {
        if (!CoOConfig.textanimatorSkipIdleTypewriterProbe || !original) {
            return original;
        }
        if (!(style instanceof TAStyle)) {
            return original;
        }
        return ((TAStyle) style).textanimator$getTypewriterTrack() != null;
    }

    @WrapOperation(
            method = "iterateFormatted",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/StringUtils;split(Ljava/lang/String;C)[Ljava/lang/String;"
            ),
            require = 0
    )
    private static String[] coo$skipUnknownEffectTag(String content, char separator, Operation<String[]> original) {
        if (CoOConfig.textanimatorSkipUnknownEffectTags && !TextAnimatorTagFilter.mayBeEffect(content)) {
            return TextAnimatorTagFilter.NO_TAG;
        }
        return original.call(content, separator);
    }
}
