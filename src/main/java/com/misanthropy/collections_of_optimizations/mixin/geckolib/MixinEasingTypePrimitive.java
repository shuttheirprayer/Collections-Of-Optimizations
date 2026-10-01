package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.eliotlash.mclib.utils.Interpolations;
import com.misanthropy.collections_of_optimizations.core.GeckoEasingPrimitives;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.keyframe.AnimationPoint;

@Mixin(value = EasingType.class, remap = false)
public interface MixinEasingTypePrimitive {

    @Overwrite(remap = false)
    default double apply(AnimationPoint animationPoint, Double easingValue, double lerpValue) {
        if (animationPoint.currentTick() >= animationPoint.transitionLength()) {
            return (float) animationPoint.animationEndValue();
        }
        Double2DoubleFunction transformer = ((EasingType) (Object) this).buildTransformer(easingValue);
        return Interpolations.lerp(animationPoint.animationStartValue(), animationPoint.animationEndValue(),
                GeckoEasingPrimitives.apply(transformer, lerpValue));
    }

    @Overwrite(remap = false)
    static Double2DoubleFunction easeOut(Double2DoubleFunction function) {
        return time -> 1.0D - GeckoEasingPrimitives.apply(function, 1.0D - time);
    }

    @Overwrite(remap = false)
    static Double2DoubleFunction easeInOut(Double2DoubleFunction function) {
        return time -> time < 0.5D
                ? GeckoEasingPrimitives.apply(function, time * 2.0D) / 2.0D
                : 1.0D - GeckoEasingPrimitives.apply(function, (1.0D - time) * 2.0D) / 2.0D;
    }

    @Overwrite(remap = false)
    static Double2DoubleFunction bounce(Double n) {
        double n2 = n == null ? 0.5D : n;
        Double2DoubleFunction one = x -> 7.5625D * x * x;
        Double2DoubleFunction two = x -> 30.25D * n2 * Math.pow(x - (double) 0.54545456F, 2.0D) + 1.0D - n2;
        Double2DoubleFunction three = x -> 121.0D * n2 * n2 * Math.pow(x - (double) 0.8181818F, 2.0D) + 1.0D - n2 * n2;
        Double2DoubleFunction four = x -> 484.0D * n2 * n2 * n2 * Math.pow(x - (double) 0.95454544F, 2.0D) + 1.0D - n2 * n2 * n2;
        return t -> Math.min(
                Math.min(GeckoEasingPrimitives.apply(one, t), GeckoEasingPrimitives.apply(two, t)),
                Math.min(GeckoEasingPrimitives.apply(three, t), GeckoEasingPrimitives.apply(four, t)));
    }
}
