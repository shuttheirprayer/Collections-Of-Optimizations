package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.NumberSyntax;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import traben.entity_model_features.models.animation.math.expression_tree.MathExpressionParser;

@Mixin(value = MathExpressionParser.class, remap = false)
public abstract class MixinMathExpressionParserTokens {

    @Redirect(
            method = "readVariableOrConstant",
            at = @At(value = "INVOKE", target = "Ljava/lang/Float;parseFloat(Ljava/lang/String;)F"),
            require = 0
    )
    private float coo$parseWithoutThrowing(String token) {
        return CoOConfig.emfParseTokensWithoutExceptions ? NumberSyntax.parseFloat(token) : Float.parseFloat(token);
    }
}
