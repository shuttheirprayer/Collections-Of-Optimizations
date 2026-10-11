package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.keyframe.AnimationPoint;
import software.bernie.geckolib.core.keyframe.Keyframe;
import software.bernie.geckolib.core.keyframe.KeyframeLocation;
import software.bernie.geckolib.core.object.Axis;

import java.util.List;
import java.util.RandomAccess;

@Mixin(value = AnimationController.class, remap = false)
public abstract class MixinAnimationControllerKeyframes {

    @Shadow
    private KeyframeLocation<Keyframe<IValue>> getCurrentKeyFrameLocation(List<Keyframe<IValue>> frames, double ageInTicks) {
        throw new AssertionError();
    }

    @Overwrite
    private AnimationPoint getAnimationPointAtTick(List<Keyframe<IValue>> frames, double tick, boolean isRotation, Axis axis) {
        Keyframe<IValue> frame = null;
        double startTick = tick;
        if (CoOConfig.geckolibKeyframeLocals && frames instanceof RandomAccess) {
            double total = 0.0;
            for (int i = 0, size = frames.size(); i < size; i++) {
                Keyframe<IValue> candidate = frames.get(i);
                total += candidate.length();
                if (total > tick) {
                    frame = candidate;
                    startTick = tick - (total - candidate.length());
                    break;
                }
            }
            if (frame == null) {
                frame = frames.get(frames.size() - 1);
            }
        } else {
            KeyframeLocation<Keyframe<IValue>> location = getCurrentKeyFrameLocation(frames, tick);
            frame = location.keyframe();
            startTick = location.startTick();
        }

        IValue start = frame.startValue();
        IValue end = frame.endValue();
        double startValue = start.get();
        double endValue = end.get();
        if (isRotation) {
            boolean flip = axis == Axis.X || axis == Axis.Y;
            if (!(start instanceof Constant)) {
                startValue = Math.toRadians(startValue);
                if (flip) {
                    startValue *= -1.0;
                }
            }
            if (!(end instanceof Constant)) {
                endValue = Math.toRadians(endValue);
                if (flip) {
                    endValue *= -1.0;
                }
            }
        }
        return new AnimationPoint(frame, startTick, frame.length(), startValue, endValue);
    }
}
