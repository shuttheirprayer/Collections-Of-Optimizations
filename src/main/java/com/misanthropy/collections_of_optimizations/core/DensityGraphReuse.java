package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongToIntFunction;

public final class DensityGraphReuse {

    private enum Kind { SIMPLE, REBUILD, CLAMP, MAPPED, MUL_OR_ADD, HOLDER, MARKER, CACHE, OTHER }

    private static final String VANILLA = "net.minecraft.world.level.levelgen.DensityFunctions$";
    private static final String LARION = "com.misanthropy.linggango.larionforgeport.density_function_types.";

    private static final Set<String> REBUILD = Set.of(
            VANILLA + "Ap2", VANILLA + "BlendDensity", VANILLA + "RangeChoice", VANILLA + "ShiftedNoise",
            VANILLA + "WeirdScaledSampler", VANILLA + "Spline", VANILLA + "Noise", VANILLA + "Shift",
            VANILLA + "ShiftA", VANILLA + "ShiftB",
            LARION + "Division", LARION + "Signum", LARION + "Sine", LARION + "Sqrt");

    private static final Set<String> READS_Y = Set.of(
            VANILLA + "YClampedGradient", VANILLA + "Shift", VANILLA + "WeirdScaledSampler",
            "net.minecraft.world.level.levelgen.synth.BlendedNoise");

    private static final Set<String> FLAT = Set.of(
            VANILLA + "Constant", VANILLA + "BlendAlpha", VANILLA + "BlendOffset", VANILLA + "EndIslandDensityFunction",
            VANILLA + "ShiftA", VANILLA + "ShiftB", LARION + "XCoord", LARION + "ZCoord");

    private static final Set<String> TRANSPARENT = Set.of(
            VANILLA + "Ap2", VANILLA + "MulOrAdd", VANILLA + "Mapped", VANILLA + "Clamp", VANILLA + "RangeChoice",
            VANILLA + "BlendDensity", VANILLA + "Spline", VANILLA + "HolderHolder", VANILLA + "Marker",
            LARION + "Division", LARION + "Signum", LARION + "Sine", LARION + "Sqrt");

    private static final ClassValue<Kind> KIND = new ClassValue<>() {
        @Override
        protected Kind computeValue(Class<?> type) {
            return kindOf(type);
        }
    };

    private static final ClassValue<MethodHandle[]> COMPONENTS = new ClassValue<>() {
        @Override
        protected MethodHandle[] computeValue(Class<?> type) {
            return componentsOf(type);
        }
    };

    private static final String MAP_ALL = mapAllName();
    private static final Object FLAT_CACHE = markerType(DensityFunctions.flatCache(DensityFunctions.zero()));
    private static final Object CACHE_2D = markerType(DensityFunctions.cache2d(DensityFunctions.zero()));
    private static final Object CACHE_ALL_IN_CELL = markerType(DensityFunctions.cacheAllInCell(DensityFunctions.zero()));
    private static final Object ADD = component(DensityFunctions.add(DensityFunctions.yClampedGradient(0, 1, 0, 1), DensityFunctions.yClampedGradient(0, 1, 0, 1)), 0);
    private static final Object MUL_OR_ADD_ADD = component(DensityFunctions.add(DensityFunctions.constant(1.0D), DensityFunctions.yClampedGradient(0, 1, 0, 1)), 0);

    private DensityGraphReuse() {
    }

    public interface PlanHolder {
        Plan coo$densityPlan();

        void coo$setDensityPlan(Plan plan);
    }

    public static Plan plan(RandomState randomState) {
        PlanHolder holder = (PlanHolder) (Object) randomState;
        NoiseRouter router = randomState.router();
        Plan plan = holder.coo$densityPlan();
        if (plan != null && plan.router == router) {
            return plan;
        }
        synchronized (holder) {
            plan = holder.coo$densityPlan();
            if (plan == null || plan.router != router) {
                plan = new Plan(router);
                holder.coo$setDensityPlan(plan);
            }
            return plan;
        }
    }

    public static FirstPass firstPass(RandomState randomState, DensityFunction.Visitor visitor) {
        Plan plan = plan(randomState);
        return plan.reuseWorthwhile ? new FirstPass(plan, visitor) : null;
    }

    public static SurfaceTable surfaceTable(RandomState randomState, NoiseSettings settings) {
        Plan plan = plan(randomState);
        return plan.surfaceShareable ? plan.tables.computeIfAbsent(settings, key -> new SurfaceTable()) : null;
    }

    public static final class Plan {
        final NoiseRouter router;
        final IdentityHashMap<DensityFunction, Boolean> stable = new IdentityHashMap<>();
        final boolean reuseWorthwhile;
        final boolean surfaceShareable;
        final Map<NoiseSettings, SurfaceTable> tables = new ConcurrentHashMap<>();

        Plan(NoiseRouter router) {
            this.router = router;
            for (DensityFunction root : new DensityFunction[]{router.barrierNoise(), router.fluidLevelFloodednessNoise(),
                    router.fluidLevelSpreadNoise(), router.lavaNoise(), router.temperature(), router.vegetation(),
                    router.continents(), router.erosion(), router.depth(), router.ridges(),
                    router.initialDensityWithoutJaggedness(), router.finalDensity(), router.veinToggle(),
                    router.veinRidged(), router.veinGap()}) {
                stable(root);
            }
            IdentityHashMap<DensityFunction, Long> sizes = new IdentityHashMap<>();
            long visits = 0;
            for (DensityFunction root : new DensityFunction[]{router.barrierNoise(), router.fluidLevelFloodednessNoise(),
                    router.fluidLevelSpreadNoise(), router.lavaNoise(), router.temperature(), router.vegetation(),
                    router.continents(), router.erosion(), router.depth(), router.ridges(),
                    router.initialDensityWithoutJaggedness(), router.finalDensity(), router.veinToggle(),
                    router.veinRidged(), router.veinGap()}) {
                visits += visits(root, sizes);
            }
            this.reuseWorthwhile = visits >= 2L * this.stable.size();
            this.surfaceShareable = pointPure(router.initialDensityWithoutJaggedness(), new IdentityHashMap<>());
        }

        private boolean stable(DensityFunction node) {
            Boolean known = this.stable.get(node);
            if (known != null) {
                return known;
            }
            this.stable.put(node, Boolean.FALSE);
            boolean result = KIND.get(node.getClass()) != Kind.OTHER;
            if (result) {
                for (DensityFunction child : children(node)) {
                    if (child == null || !stable(child)) {
                        result = false;
                    }
                }
            }
            this.stable.put(node, result);
            return result;
        }
    }

    private static long visits(DensityFunction node, IdentityHashMap<DensityFunction, Long> sizes) {
        Long known = sizes.get(node);
        if (known != null) {
            return known;
        }
        long size = 1;
        for (DensityFunction child : children(node)) {
            if (child != null) {
                size += visits(child, sizes);
            }
        }
        sizes.put(node, size);
        return size;
    }

    public static final class FirstPass implements DensityFunction.Visitor {
        private final Plan plan;
        private final DensityFunction.Visitor delegate;
        private final IdentityHashMap<DensityFunction, DensityFunction> holders = new IdentityHashMap<>();
        private final IdentityHashMap<DensityFunction, Boolean> selfMapped = new IdentityHashMap<>();
        private final IdentityHashMap<DensityFunction, DensityFunction> markerKeys = new IdentityHashMap<>();
        private IdentityHashMap<DensityFunction, Boolean> identical;
        private IdentityHashMap<DensityFunction, Boolean> equal;

        FirstPass(Plan plan, DensityFunction.Visitor delegate) {
            this.plan = plan;
            this.delegate = delegate;
        }

        @Override
        public DensityFunction apply(DensityFunction function) {
            DensityFunction result = this.delegate.apply(function);
            if (result == function) {
                this.selfMapped.put(result, Boolean.TRUE);
            } else if (KIND.get(function.getClass()) == Kind.MARKER) {
                this.markerKeys.put(result, function);
            }
            return result;
        }

        @Override
        public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noise) {
            return this.delegate.visitNoise(noise);
        }

        public DensityFunction mappedHolder(DensityFunction target) {
            return this.holders.get(target);
        }

        public void rememberHolder(DensityFunction target, DensityFunction mapped) {
            if (this.plan.stable.get(target) == Boolean.TRUE) {
                this.holders.put(target, mapped);
            }
        }

        public DensityFunction secondPass(DensityFunction root, DensityFunction.Visitor visitor) {
            if (KIND.get(root.getClass()) != Kind.MARKER || markerType(root) != CACHE_ALL_IN_CELL) {
                return null;
            }
            DensityFunction sum = ((DensityFunctions.MarkerOrMarked) root).wrapped();
            if (!sum.getClass().getName().equals(VANILLA + "Ap2") || component(sum, 0) != ADD) {
                return null;
            }
            DensityFunction finalDensity = (DensityFunction) component(sum, 1);
            DensityFunction beardifier = (DensityFunction) component(sum, 2);
            if (!beardifier.getClass().getName().equals(VANILLA + "BeardifierMarker")) {
                return null;
            }
            this.identical = new IdentityHashMap<>();
            this.equal = new IdentityHashMap<>();
            boolean reusable = identical(finalDensity);
            this.identical = null;
            this.equal = null;
            if (!reusable) {
                return null;
            }
            DensityFunction mappedBeardifier = visitor.apply(beardifier);
            DensityFunction mappedSum = visitor.apply(DensityFunctions.add(finalDensity, mappedBeardifier));
            return visitor.apply(DensityFunctions.cacheAllInCell(mappedSum));
        }

        private boolean identical(DensityFunction node) {
            Boolean known = this.identical.get(node);
            if (known != null) {
                return known;
            }
            this.identical.put(node, Boolean.FALSE);
            boolean result = switch (KIND.get(node.getClass())) {
                case SIMPLE -> this.selfMapped.containsKey(node);
                case REBUILD -> this.selfMapped.containsKey(node) && childrenEqual(node);
                case CACHE -> cacheMapsBack(node);
                default -> false;
            };
            this.identical.put(node, result);
            return result;
        }

        private boolean equal(DensityFunction node) {
            Boolean known = this.equal.get(node);
            if (known != null) {
                return known;
            }
            this.equal.put(node, Boolean.FALSE);
            boolean result = identical(node) || switch (KIND.get(node.getClass())) {
                case CLAMP -> equal((DensityFunction) component(node, 0));
                case MAPPED -> equal((DensityFunction) component(node, 1));
                case MUL_OR_ADD -> equal((DensityFunction) component(node, 1)) && mulOrAddBoundsMatch(node);
                default -> false;
            };
            this.equal.put(node, result);
            return result;
        }

        private boolean childrenEqual(DensityFunction node) {
            for (DensityFunction child : children(node)) {
                if (child == null || !equal(child)) {
                    return false;
                }
            }
            return true;
        }

        private boolean cacheMapsBack(DensityFunction node) {
            DensityFunction key = this.markerKeys.get(node);
            if (key == null) {
                return false;
            }
            DensityFunctions.MarkerOrMarked cache = (DensityFunctions.MarkerOrMarked) node;
            DensityFunctions.MarkerOrMarked marker = (DensityFunctions.MarkerOrMarked) key;
            return cache.type() == marker.type() && cache.wrapped() == marker.wrapped() && equal(cache.wrapped());
        }
    }

    public static final class SurfaceTable {
        private static final int STRIPES = 64;
        private static final int STRIPE_LIMIT = 1024;
        private static final int ABSENT = Integer.MIN_VALUE;

        private final Long2IntOpenHashMap[] stripes = new Long2IntOpenHashMap[STRIPES];

        SurfaceTable() {
            for (int i = 0; i < STRIPES; i++) {
                this.stripes[i] = new Long2IntOpenHashMap();
                this.stripes[i].defaultReturnValue(ABSENT);
            }
        }

        public int get(long column, LongToIntFunction compute) {
            Long2IntOpenHashMap stripe = this.stripes[(int) (HashCommon.mix(column) & (STRIPES - 1))];
            int value;
            synchronized (stripe) {
                value = stripe.get(column);
            }
            if (value != ABSENT) {
                return value;
            }
            value = compute.applyAsInt(column);
            synchronized (stripe) {
                if (stripe.size() >= STRIPE_LIMIT) {
                    stripe.clear();
                }
                stripe.put(column, value);
            }
            return value;
        }
    }

    private static boolean mulOrAddBoundsMatch(DensityFunction node) {
        DensityFunction input = (DensityFunction) component(node, 1);
        double argument = (Double) component(node, 4);
        double inputMin = input.minValue();
        double inputMax = input.maxValue();
        double min;
        double max;
        if (component(node, 0) == MUL_OR_ADD_ADD) {
            min = inputMin + argument;
            max = inputMax + argument;
        } else if (argument >= 0.0D) {
            min = inputMin * argument;
            max = inputMax * argument;
        } else {
            min = inputMax * argument;
            max = inputMin * argument;
        }
        return Double.compare(min, (Double) component(node, 2)) == 0 && Double.compare(max, (Double) component(node, 3)) == 0;
    }

    private static boolean pointPure(DensityFunction node, IdentityHashMap<DensityFunction, Boolean> seen) {
        Boolean known = seen.get(node);
        if (known != null) {
            return known;
        }
        seen.put(node, Boolean.FALSE);
        String name = node.getClass().getName();
        boolean result;
        if (READS_Y.contains(name) || FLAT.contains(name) || name.equals(VANILLA + "Noise") || name.equals(VANILLA + "ShiftedNoise")) {
            result = true;
            for (DensityFunction child : children(node)) {
                result &= child != null && pointPure(child, seen);
            }
        } else if (TRANSPARENT.contains(name)) {
            result = true;
            for (DensityFunction child : children(node)) {
                result &= child != null && pointPure(child, seen);
            }
            if (result && name.equals(VANILLA + "Marker")) {
                Object type = markerType(node);
                if (type == FLAT_CACHE || type == CACHE_2D) {
                    result = !readsY(((DensityFunctions.MarkerOrMarked) node).wrapped(), new IdentityHashMap<>());
                }
            }
        } else {
            result = false;
        }
        seen.put(node, result);
        return result;
    }

    private static boolean readsY(DensityFunction node, IdentityHashMap<DensityFunction, Boolean> seen) {
        Boolean known = seen.get(node);
        if (known != null) {
            return known;
        }
        seen.put(node, Boolean.TRUE);
        String name = node.getClass().getName();
        boolean result;
        if (READS_Y.contains(name)) {
            result = true;
        } else if (FLAT.contains(name)) {
            result = false;
        } else if (name.equals(VANILLA + "Noise")) {
            result = (Double) component(node, 2) != 0.0D;
        } else if (name.equals(VANILLA + "ShiftedNoise")) {
            result = (Double) component(node, 4) != 0.0D;
            for (DensityFunction child : children(node)) {
                result |= child == null || readsY(child, seen);
            }
        } else if (TRANSPARENT.contains(name)) {
            result = false;
            for (DensityFunction child : children(node)) {
                result |= child == null || readsY(child, seen);
            }
        } else {
            result = true;
        }
        seen.put(node, result);
        return result;
    }

    private static List<DensityFunction> children(DensityFunction node) {
        List<DensityFunction> out = new ArrayList<>(3);
        switch (KIND.get(node.getClass())) {
            case CACHE -> out.add(((DensityFunctions.MarkerOrMarked) node).wrapped());
            case SIMPLE, OTHER -> {
            }
            default -> {
                for (MethodHandle accessor : COMPONENTS.get(node.getClass())) {
                    collect(read(accessor, node), out);
                }
            }
        }
        return out;
    }

    private static void collect(Object value, List<DensityFunction> out) {
        if (value instanceof DensityFunction function) {
            out.add(function);
        } else if (value instanceof Holder<?> holder) {
            out.add(holder.value() instanceof DensityFunction function ? function : null);
        } else if (value instanceof CubicSpline.Multipoint<?, ?> spline) {
            if (spline.coordinate() instanceof DensityFunctions.Spline.Coordinate coordinate) {
                collect(coordinate.function(), out);
            } else {
                out.add(null);
            }
            for (Object child : spline.values()) {
                collect(child, out);
            }
        } else if (value instanceof CubicSpline<?, ?> && !(value instanceof CubicSpline.Constant<?, ?>)) {
            out.add(null);
        }
    }

    private static Kind kindOf(Class<?> type) {
        String name = type.getName();
        if (REBUILD.contains(name)) {
            return type.isRecord() ? Kind.REBUILD : Kind.OTHER;
        }
        switch (name) {
            case VANILLA + "Clamp":
                return Kind.CLAMP;
            case VANILLA + "Mapped":
                return Kind.MAPPED;
            case VANILLA + "MulOrAdd":
                return Kind.MUL_OR_ADD;
            case VANILLA + "HolderHolder":
                return Kind.HOLDER;
            case VANILLA + "Marker":
                return Kind.MARKER;
            default:
                break;
        }
        try {
            Class<?> declaring = type.getMethod(MAP_ALL, DensityFunction.Visitor.class).getDeclaringClass();
            if (declaring == DensityFunction.SimpleFunction.class) {
                return Kind.SIMPLE;
            }
            if (declaring == DensityFunctions.MarkerOrMarked.class) {
                return Kind.CACHE;
            }
        } catch (NoSuchMethodException ignored) {
        }
        return Kind.OTHER;
    }

    private static MethodHandle[] componentsOf(Class<?> type) {
        if (!type.isRecord()) {
            return new MethodHandle[0];
        }
        RecordComponent[] components = type.getRecordComponents();
        MethodHandle[] out = new MethodHandle[components.length];
        try {
            for (int i = 0; i < components.length; i++) {
                Field field = type.getDeclaredField(components[i].getName());
                field.setAccessible(true);
                out[i] = MethodHandles.lookup().unreflectGetter(field).asType(MethodType.methodType(Object.class, Object.class));
            }
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new IllegalStateException(exception);
        }
        return out;
    }

    private static Object component(DensityFunction node, int index) {
        return read(COMPONENTS.get(node.getClass())[index], node);
    }

    private static Object read(MethodHandle accessor, Object node) {
        try {
            return (Object) accessor.invokeExact(node);
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }

    private static Object markerType(DensityFunction marker) {
        return ((DensityFunctions.MarkerOrMarked) marker).type();
    }

    private static String mapAllName() {
        for (Method method : DensityFunction.class.getMethods()) {
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == DensityFunction.Visitor.class
                    && method.getReturnType() == DensityFunction.class) {
                return method.getName();
            }
        }
        throw new IllegalStateException("DensityFunction has no mapAll");
    }
}
