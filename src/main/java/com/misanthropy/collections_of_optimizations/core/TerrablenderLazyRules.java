package com.misanthropy.collections_of_optimizations.core;

import com.google.common.collect.MapMaker;
import com.mojang.logging.LogUtils;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public final class TerrablenderLazyRules {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String PREFIX = "net.minecraft.world.level.levelgen.SurfaceRules$";

    private static final Set<String> BRANCHES = Set.of(
            PREFIX + "SequenceRuleSource",
            PREFIX + "TestRuleSource",
            PREFIX + "NotConditionSource"
    );

    private static final Set<String> LEAVES = Set.of(
            PREFIX + "BlockRuleSource",
            PREFIX + "Bandlands",
            PREFIX + "BiomeConditionSource",
            PREFIX + "NoiseThresholdConditionSource",
            PREFIX + "VerticalGradientConditionSource",
            PREFIX + "YConditionSource",
            PREFIX + "WaterConditionSource",
            PREFIX + "StoneDepthCheck",
            PREFIX + "Hole",
            PREFIX + "Steep",
            PREFIX + "Temperature",
            PREFIX + "AbovePreliminarySurface"
    );

    private static final Set<String> PRODUCTS = Set.of(
            PREFIX + "Context",
            PREFIX + "LazyCondition",
            PREFIX + "LazyXZCondition",
            PREFIX + "LazyYCondition",
            PREFIX + "SequenceRule",
            PREFIX + "TestRule",
            PREFIX + "NotCondition",
            PREFIX + "StateRule",
            PREFIX + "BiomeConditionSource$1BiomeCondition",
            PREFIX + "NoiseThresholdConditionSource$1NoiseThresholdCondition",
            PREFIX + "VerticalGradientConditionSource$1VerticalGradientCondition",
            PREFIX + "YConditionSource$1YCondition",
            PREFIX + "WaterConditionSource$1WaterCondition",
            PREFIX + "StoneDepthCheck$1StoneDepthCondition"
    );

    private static final Set<String> VERIFIED_MIXINS = Set.of(
            "org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules.BiomeConditionSourceMixin",
            "org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules.SurfaceRulesContextMixin",
            "org.embeddedt.modernfix.common.mixin.perf.worldgen_allocation.SurfaceRulesContextMixin",
            "org.embeddedt.modernfix.common.mixin.perf.worldgen_allocation.SurfaceRulesMixin",
            "org.embeddedt.modernfix.common.mixin.perf.worldgen_allocation.SequenceRuleMixin",
            "com.misanthropy.collections_of_optimizations.mixin.terrablender.SurfaceContextAccessor"
    );

    private static final ConcurrentMap<Object, Boolean> VERDICTS = new MapMaker().weakKeys().makeMap();

    private static volatile Boolean classesClean;

    private TerrablenderLazyRules() {
    }

    public static Map.Entry<String, ?> defer(Map.Entry<String, ?> entry) {
        Object value = entry.getValue();
        if (!(value instanceof SurfaceRules.RuleSource source) || !deferrable(source)) {
            return entry;
        }
        return Map.entry(entry.getKey(), new DeferredSource(source));
    }

    private static boolean deferrable(SurfaceRules.RuleSource source) {
        if (!classesClean()) {
            return false;
        }
        Boolean verdict = VERDICTS.get(source);
        if (verdict == null) {
            boolean pure;
            try {
                pure = pureTree(source, new IdentityHashMap<>(), 0);
            } catch (Throwable throwable) {
                pure = false;
            }
            verdict = pure;
            VERDICTS.put(source, verdict);
        }
        return verdict;
    }

    private static boolean pureTree(Object node, IdentityHashMap<Object, Boolean> seen, int depth) throws IllegalAccessException {
        if (node == null || depth > 512) {
            return false;
        }
        if (seen.put(node, Boolean.TRUE) != null) {
            return true;
        }
        String name = node.getClass().getName();
        if (LEAVES.contains(name)) {
            return true;
        }
        if (!BRANCHES.contains(name)) {
            return false;
        }
        for (Field field : node.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Object child = field.get(node);
            if (child instanceof List<?> list) {
                for (Object element : list) {
                    if (!pureTree(element, seen, depth + 1)) {
                        return false;
                    }
                }
            } else if (!pureTree(child, seen, depth + 1)) {
                return false;
            }
        }
        return true;
    }

    private static boolean classesClean() {
        Boolean clean = classesClean;
        if (clean != null) {
            return clean;
        }
        synchronized (TerrablenderLazyRules.class) {
            if (classesClean != null) {
                return classesClean;
            }
            String reason = null;
            try {
                ClassLoader loader = SurfaceRules.class.getClassLoader();
                outer:
                for (Set<String> group : List.of(BRANCHES, LEAVES, PRODUCTS)) {
                    for (String className : group) {
                        for (Method method : Class.forName(className, false, loader).getDeclaredMethods()) {
                            MixinMerged merged = method.getAnnotation(MixinMerged.class);
                            if (merged != null && !VERIFIED_MIXINS.contains(merged.mixin())) {
                                reason = className.substring(PREFIX.length()) + " carries " + merged.mixin();
                                break outer;
                            }
                        }
                    }
                }
            } catch (Throwable throwable) {
                reason = throwable.toString();
            }
            if (reason != null) {
                LOGGER.info("Collections of Optimizations: TerraBlender namespaces stay built up front ({})", reason);
            }
            classesClean = reason == null;
            return classesClean;
        }
    }

    private static final class DeferredSource implements SurfaceRules.RuleSource {
        private final SurfaceRules.RuleSource source;

        private DeferredSource(SurfaceRules.RuleSource source) {
            this.source = source;
        }

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return this.source.codec();
        }

        @Override
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            return new DeferredRule(this.source, context);
        }
    }

    private static final class DeferredRule implements SurfaceRules.SurfaceRule {
        private final SurfaceRules.RuleSource source;
        private final SurfaceRules.Context context;
        private SurfaceRules.SurfaceRule built;

        private DeferredRule(SurfaceRules.RuleSource source, SurfaceRules.Context context) {
            this.source = source;
            this.context = context;
        }

        @Override
        public BlockState tryApply(int x, int y, int z) {
            SurfaceRules.SurfaceRule rule = this.built;
            if (rule == null) {
                rule = this.source.apply(this.context);
                this.built = rule;
            }
            return rule.tryApply(x, y, z);
        }
    }
}
