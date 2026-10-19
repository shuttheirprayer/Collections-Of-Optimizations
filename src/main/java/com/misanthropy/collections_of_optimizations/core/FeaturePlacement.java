package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.vanilla.PlacementFilterInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
                if (!((PlacementFilterInvoker) filter).coo$shouldPlace(context, random, pos)) {
                    return false;
                }
                index++;
                continue;
            }
            Walk walk = new Walk(modifiers, index + 1, feature, context, random);
            try (Stream<BlockPos> positions = modifier.getPositions(context, random, pos)) {
                positions.sequential().forEach(walk);
            }
            return walk.placed;
        }
        return feature.place(context.getLevel(), context.generator(), random, pos);
    }

    private static final class Walk implements Consumer<BlockPos> {

        private final List<PlacementModifier> modifiers;
        private final int next;
        private final ConfiguredFeature<?, ?> feature;
        private final PlacementContext context;
        private final RandomSource random;
        private boolean placed;

        private Walk(List<PlacementModifier> modifiers, int next, ConfiguredFeature<?, ?> feature, PlacementContext context,
                     RandomSource random) {
            this.modifiers = modifiers;
            this.next = next;
            this.feature = feature;
            this.context = context;
            this.random = random;
        }

        @Override
        public void accept(BlockPos pos) {
            if (step(this.modifiers, this.next, this.feature, this.context, this.random, pos)) {
                this.placed = true;
            }
        }
    }
}
