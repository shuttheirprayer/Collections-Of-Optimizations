package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.StringCharIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.animation.math.expression_tree.MathExpressionParser;

import java.util.Iterator;
import java.util.stream.Stream;

@Mixin(value = MathExpressionParser.class, remap = false)
public abstract class MixinEmfParserCharIterator {

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;iterator()Ljava/util/Iterator;"),
            require = 0
    )
    private Iterator<?> coo$plainIterator(Stream<?> stream, Operation<Iterator<?>> original, @Local(argsOnly = true, ordinal = 0) String text) {
        return CoOConfig.emfCompiledPatterns ? new StringCharIterator(text) : original.call(stream);
    }
}
