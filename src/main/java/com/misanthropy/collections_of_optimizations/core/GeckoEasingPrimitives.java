package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import software.bernie.geckolib.core.animation.EasingType;

public final class GeckoEasingPrimitives {

    private static final ClassValue<Boolean> OWN_LAMBDA = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return type.isHidden() && type.getNestHost() == EasingType.class;
        }
    };

    private GeckoEasingPrimitives() {
    }

    public static double apply(Double2DoubleFunction function, double value) {
        if (CoOConfig.geckolibPrimitiveEasing && OWN_LAMBDA.get(function.getClass())) {
            return function.get(value);
        }
        return function.apply(value);
    }
}
