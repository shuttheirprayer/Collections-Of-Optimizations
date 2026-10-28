package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class FlatClimateTree {
    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int PARAMETERS = 7;
    private static final int CHECKED_TARGETS = 512;

    public static final FlatClimateTree UNUSABLE = new FlatClimateTree();

    private static final Map<Class<?>, Boolean> METRICS = new ConcurrentHashMap<>();
    private static volatile boolean checking;

    private static Class<?> leafClass;
    private static Field rootField;
    private static Field lastField;
    private static Field childrenField;
    private static Field valueField;
    private static Field spaceField;
    private static Method metricMethod;
    private static Method searchMethod;
    private static boolean reflectionReady;
    private static boolean reflectionBroken;

    private final long[] mins;
    private final long[] maxs;
    private final int[] firstChild;
    private final int[] childCount;
    private final Object[] nodes;
    private final Object[] values;
    private final ThreadLocal<Object> last;

    private FlatClimateTree() {
        mins = maxs = null;
        firstChild = childCount = null;
        nodes = values = null;
        last = null;
    }

    private FlatClimateTree(List<Object> order, int[] firstChild, int[] childCount, ThreadLocal<Object> last) throws IllegalAccessException {
        int n = order.size();
        this.mins = new long[n * PARAMETERS];
        this.maxs = new long[n * PARAMETERS];
        this.firstChild = firstChild;
        this.childCount = childCount;
        this.nodes = order.toArray();
        this.values = new Object[n];
        this.last = last;
        for (int i = 0; i < n; i++) {
            Object node = nodes[i];
            Climate.Parameter[] space = (Climate.Parameter[]) spaceField.get(node);
            for (int k = 0; k < PARAMETERS; k++) {
                mins[i * PARAMETERS + k] = space[k].min();
                maxs[i * PARAMETERS + k] = space[k].max();
            }
            if (childCount[i] == 0) {
                values[i] = valueField.get(node);
                ((FlatClimateNode) node).coo$setFlatIndex(i);
            }
        }
    }

    public static boolean checking() {
        return checking;
    }

    public static FlatClimateTree build(Object tree, Object metric) {
        try {
            if (!reflection()) {
                return UNUSABLE;
            }
            Function<Climate.TargetPoint, Object> vanillaSearch = target -> {
                try {
                    return searchMethod.invoke(tree, target, metric);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            };
            Object root = rootField.get(tree);
            @SuppressWarnings("unchecked")
            ThreadLocal<Object> last = (ThreadLocal<Object>) lastField.get(tree);
            List<Object> order = new ArrayList<>();
            order.add(root);
            List<int[]> ranges = new ArrayList<>();
            for (int i = 0; i < order.size(); i++) {
                Object node = order.get(i);
                if (leafClass.isInstance(node)) {
                    ranges.add(new int[]{0, 0});
                    continue;
                }
                Object[] children = (Object[]) childrenField.get(node);
                ranges.add(new int[]{order.size(), children.length});
                for (Object child : children) {
                    order.add(child);
                }
            }
            int[] first = new int[order.size()];
            int[] count = new int[order.size()];
            for (int i = 0; i < first.length; i++) {
                first[i] = ranges.get(i)[0];
                count[i] = ranges.get(i)[1];
            }
            FlatClimateTree flat = new FlatClimateTree(order, first, count, last);
            if (!flat.matchesVanilla(vanillaSearch)) {
                LOGGER.warn("Flat biome search disagreed with the vanilla search on a {} entry biome tree, that tree keeps the vanilla search", order.size());
                return UNUSABLE;
            }
            return flat;
        } catch (Throwable t) {
            LOGGER.warn("Flat biome search is off for one biome tree", t);
            return UNUSABLE;
        }
    }

    public boolean usable() {
        return this != UNUSABLE;
    }

    public boolean acceptsMetric(Object metric) {
        Class<?> type = metric.getClass();
        Boolean known = METRICS.get(type);
        if (known == null) {
            known = sameAsNodeDistance(metric);
            METRICS.put(type, known);
            if (!known) {
                LOGGER.info("Biome tree searches through {} keep the vanilla search", type.getName());
            }
        }
        return known;
    }

    public Object search(Climate.TargetPoint target) {
        long[] t = parameters(target);
        Object previous = last.get();
        int start = -1;
        long startDistance = Long.MAX_VALUE;
        if (previous instanceof FlatClimateNode indexed) {
            start = indexed.coo$flatIndex();
            if (start < 0 || start >= nodes.length || nodes[start] != previous) {
                return null;
            }
            startDistance = distance(start, t);
        } else if (previous != null) {
            return null;
        }
        long[] out = new long[1];
        int leaf = childCount[0] == 0 ? 0 : search(0, t, start, startDistance, out);
        last.set(nodes[leaf]);
        return values[leaf];
    }

    private static long[] parameters(Climate.TargetPoint t) {
        return new long[]{t.temperature(), t.humidity(), t.continentalness(), t.erosion(), t.depth(), t.weirdness(), 0L};
    }

    private int search(int node, long[] t, int best, long bestDistance, long[] out) {
        long i = bestDistance;
        int leaf = best;
        int end = firstChild[node] + childCount[node];
        for (int child = firstChild[node]; child < end; child++) {
            long j = distance(child, t);
            if (i > j) {
                int found;
                long k;
                if (childCount[child] == 0) {
                    found = child;
                    k = j;
                } else {
                    found = search(child, t, leaf, i, out);
                    k = out[0];
                }
                if (i > k) {
                    i = k;
                    leaf = found;
                }
            }
        }
        out[0] = i;
        return leaf;
    }

    private long distance(int node, long[] t) {
        int base = node * PARAMETERS;
        long sum = 0L;
        for (int k = 0; k < PARAMETERS; k++) {
            long p = t[k];
            long above = p - maxs[base + k];
            long below = mins[base + k] - p;
            long d = above > 0L ? above : Math.max(below, 0L);
            sum += d * d;
        }
        return sum;
    }

    private boolean matchesVanilla(Function<Climate.TargetPoint, Object> vanillaSearch) {
        SplittableRandom random = new SplittableRandom(0x5EEDL ^ nodes.length);
        Object saved = last.get();
        checking = true;
        try {
            for (int n = 0; n < CHECKED_TARGETS; n++) {
                Climate.TargetPoint target = randomTarget(random, n);
                last.set(null);
                Object expected = vanillaSearch.apply(target);
                Object expectedLeaf = last.get();
                last.set(null);
                Object actual = search(target);
                if (actual != expected || last.get() != expectedLeaf) {
                    return false;
                }
                int warmIndex = random.nextInt(nodes.length);
                Object warmStart = nodes[warmIndex];
                if (childCount[warmIndex] == 0) {
                    Climate.TargetPoint next = randomTarget(random, n + 1);
                    last.set(warmStart);
                    expected = vanillaSearch.apply(next);
                    expectedLeaf = last.get();
                    last.set(warmStart);
                    actual = search(next);
                    if (actual != expected || last.get() != expectedLeaf) {
                        return false;
                    }
                }
            }
            return true;
        } finally {
            checking = false;
            last.set(saved);
        }
    }

    private Climate.TargetPoint randomTarget(SplittableRandom random, int n) {
        if ((n & 1) == 0) {
            int leaf = random.nextInt(nodes.length);
            int base = leaf * PARAMETERS;
            long[] p = new long[PARAMETERS];
            for (int k = 0; k < 6; k++) {
                long lo = mins[base + k];
                long hi = maxs[base + k];
                p[k] = hi > lo ? lo + random.nextLong(hi - lo + 1) : lo;
            }
            return new Climate.TargetPoint(p[0], p[1], p[2], p[3], p[4], p[5]);
        }
        return new Climate.TargetPoint(random.nextLong(-22000, 22001), random.nextLong(-22000, 22001),
                random.nextLong(-22000, 22001), random.nextLong(-22000, 22001),
                random.nextLong(-22000, 22001), random.nextLong(-22000, 22001));
    }

    private boolean sameAsNodeDistance(Object metric) {
        try {
            SplittableRandom random = new SplittableRandom(nodes.length);
            for (int n = 0; n < 4; n++) {
                long[] t = parameters(randomTarget(random, n));
                for (int i = 0; i < nodes.length; i++) {
                    long expected = distance(i, t);
                    long actual = (long) metricMethod.invoke(metric, nodes[i], t);
                    if (actual != expected) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static synchronized boolean reflection() {
        if (reflectionReady) {
            return true;
        }
        if (reflectionBroken) {
            return false;
        }
        try {
            Class<?> tree = Class.forName("net.minecraft.world.level.biome.Climate$RTree");
            Class<?> node = Class.forName("net.minecraft.world.level.biome.Climate$RTree$Node");
            Class<?> subTree = Class.forName("net.minecraft.world.level.biome.Climate$RTree$SubTree");
            leafClass = Class.forName("net.minecraft.world.level.biome.Climate$RTree$Leaf");
            rootField = ObfuscationReflectionHelper.findField(tree, "f_186910_");
            lastField = ObfuscationReflectionHelper.findField(tree, "f_186911_");
            childrenField = ObfuscationReflectionHelper.findField(subTree, "f_186965_");
            valueField = ObfuscationReflectionHelper.findField(leafClass, "f_186948_");
            spaceField = ObfuscationReflectionHelper.findField(node, "f_186956_");
            Class<?> metric = Class.forName("net.minecraft.world.level.biome.Climate$DistanceMetric");
            for (Method m : metric.getDeclaredMethods()) {
                if (Modifier.isAbstract(m.getModifiers())) {
                    metricMethod = m;
                }
            }
            metricMethod.setAccessible(true);
            searchMethod = ObfuscationReflectionHelper.findMethod(tree, "m_186930_", Climate.TargetPoint.class, metric);
            reflectionReady = true;
            return true;
        } catch (Throwable t) {
            reflectionBroken = true;
            LOGGER.warn("Flat biome search is off, the biome tree did not look as expected", t);
            return false;
        }
    }
}
