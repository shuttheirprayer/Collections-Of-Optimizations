package com.misanthropy.collections_of_optimizations.core;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.keyframe.Keyframe;
import software.bernie.geckolib.core.molang.expressions.MolangValue;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class GeckoShare {

    private static final long IDLE_NANOS = 10_000_000_000L;
    private static final ConcurrentHashMap<Long, Constant> CONSTANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, MolangValue> VALUES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Frame, Keyframe<IValue>> FRAMES = new ConcurrentHashMap<>();
    private static final List<IValue> NO_ARGS = ObjectLists.emptyList();
    private static volatile long lastUse;

    private GeckoShare() {
    }

    private record Frame(long length, IValue start, IValue end, EasingType type) {
    }

    public static Constant constant(Constant made) {
        lastUse = System.nanoTime();
        Constant shared = CONSTANTS.putIfAbsent(Double.doubleToRawLongBits(made.get()), made);
        return shared != null ? shared : made;
    }

    public static MolangValue value(MolangValue made) {
        if (made.getClass() != MolangValue.class || made.isReturnValue() || made.getValueHolder().getClass() != Constant.class) {
            return made;
        }
        lastUse = System.nanoTime();
        MolangValue shared = VALUES.putIfAbsent(Double.doubleToRawLongBits(made.get()), made);
        return shared != null ? shared : made;
    }

    public static Keyframe<IValue> keyframe(Keyframe<IValue> made) {
        if (!made.easingArgs().isEmpty()) {
            return made;
        }
        lastUse = System.nanoTime();
        Frame key = new Frame(Double.doubleToRawLongBits(made.length()), made.startValue(), made.endValue(), made.easingType());
        Keyframe<IValue> shared = FRAMES.get(key);
        if (shared == null) {
            Keyframe<IValue> lean = new Keyframe<>(made.length(), made.startValue(), made.endValue(), made.easingType(), NO_ARGS);
            shared = FRAMES.putIfAbsent(key, lean);
            if (shared == null) {
                shared = lean;
            }
        }
        return shared;
    }

    public static void trim() {
        if (!CONSTANTS.isEmpty() && System.nanoTime() - lastUse > IDLE_NANOS) {
            CONSTANTS.clear();
            VALUES.clear();
            FRAMES.clear();
        }
    }
}
