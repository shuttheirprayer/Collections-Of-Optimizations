package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.HeightRangePlacementAccessor;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.PlacementFilterInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class FeaturePlacement {

    private static final String BYEPREGEN_PLACEMENT = "com.moepus.byepregen.worldgen.feature.FastPlacedFeature";

    private static volatile int byepregen;

    private FeaturePlacement() {
    }

    public static boolean otherRewriteActive() {
        int state = byepregen;
        if (state == 0) {
            state = 2;
            for (Class<?> type : PlacedFeature.class.getInterfaces()) {
                if (BYEPREGEN_PLACEMENT.equals(type.getName())) {
                    state = 1;
                    break;
                }
            }
            byepregen = state;
        }
        return state == 1;
    }

    public static boolean place(List<PlacementModifier> modifiers, ConfiguredFeature<?, ?> feature, PlacementContext context,
                                RandomSource random, BlockPos pos) {
        return step(modifiers, 0, feature, context, random, pos);
    }

    private static boolean step(List<PlacementModifier> modifiers, int index, ConfiguredFeature<?, ?> feature,
                                PlacementContext context, RandomSource random, BlockPos pos) {
        int size = modifiers.size();
        while (index < size) {
            PlacementModifier modifier = modifiers.get(index);
            if (modifier instanceof PlacementFilter filter) {
                int decided = modifier.getClass() == BiomeFilter.class && CoOConfig.vanillaFeatureBiomeMasks ? FeatureBiomes.decide(context, pos) : FeatureBiomes.UNKNOWN;
                if (decided == FeatureBiomes.NO || decided == FeatureBiomes.UNKNOWN && !((PlacementFilterInvoker) filter).coo$shouldPlace(context, random, pos)) {
                    return false;
                }
                index++;
                continue;
            }
            Class<?> type = modifier.getClass();
            if (type == InSquarePlacement.class) {
                int x = random.nextInt(16) + pos.getX();
                int z = random.nextInt(16) + pos.getZ();
                pos = new BlockPos(x, pos.getY(), z);
                index++;
                continue;
            }
            if (type == HeightRangePlacement.class) {
                pos = pos.atY(((HeightRangePlacementAccessor) modifier).coo$height().sample(random, context));
                index++;
                continue;
            }
            Walks walks = WALKS.get();
            int depth = walks.depth;
            Walk walk = depth < walks.pool.length ? walks.pool[depth] : new Walk();
            if (walk == null) {
                walk = new Walk();
                walks.pool[depth] = walk;
            }
            walk.set(modifiers, index + 1, feature, context, random);
            walks.depth = depth + 1;
            try (Stream<BlockPos> positions = modifier.getPositions(context, random, pos)) {
                positions.sequential().forEach(walk);
                return walk.placed;
            } finally {
                walks.depth = depth;
                walk.set(null, 0, null, null, null);
            }
        }
        return feature.place(context.getLevel(), context.generator(), random, pos);
    }

    private static final ThreadLocal<Walks> WALKS = ThreadLocal.withInitial(Walks::new);

    private static final class Walks {
        final Walk[] pool = new Walk[32];
        int depth;
    }

    private static final class Walk implements Consumer<BlockPos> {

        private List<PlacementModifier> modifiers;
        private int next;
        private ConfiguredFeature<?, ?> feature;
        private PlacementContext context;
        private RandomSource random;
        private boolean placed;

        private void set(List<PlacementModifier> modifiers, int next, ConfiguredFeature<?, ?> feature, PlacementContext context,
                         RandomSource random) {
            this.modifiers = modifiers;
            this.next = next;
            this.feature = feature;
            this.context = context;
            this.random = random;
            this.placed = false;
        }

        @Override
        public void accept(BlockPos pos) {
            if (step(this.modifiers, this.next, this.feature, this.context, this.random, pos)) {
                this.placed = true;
            }
        }
    }
}
