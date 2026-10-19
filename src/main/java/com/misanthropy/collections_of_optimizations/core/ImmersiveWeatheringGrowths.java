package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.immersiveweathering.ConfigurableBlockGrowthAccessor;
import com.ordana.immersive_weathering.configs.CommonConfigs;
import com.ordana.immersive_weathering.data.block_growths.BlockGrowthHandler;
import com.ordana.immersive_weathering.data.block_growths.BlockPair;
import com.ordana.immersive_weathering.data.block_growths.IConditionalGrowingBlock;
import com.ordana.immersive_weathering.data.block_growths.TickSource;
import com.ordana.immersive_weathering.data.block_growths.growths.ConfigurableBlockGrowth;
import com.ordana.immersive_weathering.data.block_growths.growths.IBlockGrowth;
import com.ordana.immersive_weathering.data.position_tests.IPositionRuleTest;
import com.ordana.immersive_weathering.mixins.accessors.RandomBlockMatchTestAccessor;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class ImmersiveWeatheringGrowths {

    private static final TickSource[] SOURCES = TickSource.values();
    private static final Plan EMPTY = new Plan(new IBlockGrowth[0], new boolean[0], new int[0], new double[1]);

    private static volatile Table[] tables;
    private static volatile Plan sharedBlockTick = EMPTY;

    private ImmersiveWeatheringGrowths() {
    }

    public static void rebuild(Map<TickSource, Map<Block, Set<IBlockGrowth>>> byBlock, Map<TickSource, Set<IBlockGrowth>> universal) {
        Table[] built = new Table[SOURCES.length];
        for (TickSource source : SOURCES) {
            Set<IBlockGrowth> shared = universal.get(source);
            Plan sharedPlan = plan(shared, null);
            Reference2ObjectOpenHashMap<Block, Plan> plans = new Reference2ObjectOpenHashMap<>();
            Map<Block, Set<IBlockGrowth>> owned = byBlock.get(source);
            if (owned != null) {
                for (Map.Entry<Block, Set<IBlockGrowth>> entry : owned.entrySet()) {
                    Plan plan = plan(shared, entry.getValue());
                    if (plan != EMPTY) {
                        plans.put(entry.getKey(), plan);
                    }
                }
            }
            plans.defaultReturnValue(sharedPlan);
            built[source.ordinal()] = new Table(plans);
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            ((ImmersiveWeatheringGrowthHolder) block).coo$setGrowthPlan(null);
        }
        Table blockTick = built[TickSource.BLOCK_TICK.ordinal()];
        for (Map.Entry<Block, Plan> entry : blockTick.plans.reference2ObjectEntrySet()) {
            ((ImmersiveWeatheringGrowthHolder) entry.getKey()).coo$setGrowthPlan(entry.getValue());
        }
        sharedBlockTick = blockTick.plans.defaultReturnValue();
        tables = built;
    }

    private static Plan plan(Set<IBlockGrowth> shared, Set<IBlockGrowth> owned) {
        List<IBlockGrowth> growths = new ArrayList<>();
        if (shared != null) {
            for (IBlockGrowth growth : shared) {
                if (!(growth instanceof ConfigurableBlockGrowth configurable) || configurable.getGrowthChance() != 0.0f) {
                    growths.add(growth);
                }
            }
        }
        if (owned != null) {
            for (IBlockGrowth growth : owned) {
                if (!(growth instanceof ConfigurableBlockGrowth configurable) || configurable.getGrowthChance() != 0.0f) {
                    growths.add(growth);
                }
            }
        }
        if (growths.isEmpty()) {
            return EMPTY;
        }
        int size = growths.size();
        IBlockGrowth[] array = growths.toArray(new IBlockGrowth[0]);
        boolean[] chained = new boolean[size];
        int[] runEnd = new int[size];
        double[] keep = new double[size + 1];
        for (int i = 0; i < size; i++) {
            chained[i] = array[i] instanceof ConfigurableBlockGrowth configurable
                    && configurable.getGrowthChance() > 0.0f && configurable.getGrowthChance() < 1.0f;
        }
        for (int i = 0; i < size; ) {
            if (!chained[i]) {
                i++;
                continue;
            }
            int end = i;
            keep[i] = 1.0;
            while (end < size && chained[end]) {
                keep[end + 1] = keep[end] * (1.0 - ((ConfigurableBlockGrowth) array[end]).getGrowthChance());
                end++;
            }
            for (int k = i; k < end; k++) {
                runEnd[k] = end;
            }
            i = end;
        }
        return new Plan(array, chained, runEnd, keep);
    }

    public static boolean ready() {
        return tables != null;
    }

    public static void randomTick(LevelChunkSection section, BlockPos pos, ServerLevel level) {
        BlockState state = section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        if (!CoOConfig.immersiveweatheringGrowthTable || tables == null) {
            BlockGrowthHandler.tickBlock(TickSource.BLOCK_TICK, state, level, pos);
            return;
        }
        Block block = state.getBlock();
        Object plan = ((ImmersiveWeatheringGrowthHolder) block).coo$getGrowthPlan();
        if (plan == null) {
            plan = sharedBlockTick;
            if (plan == EMPTY) {
                return;
            }
        }
        if (!CommonConfigs.BLOCK_GROWTHS.get()) {
            return;
        }
        if (block instanceof IConditionalGrowingBlock conditional && !conditional.canGrow(state)) {
            return;
        }
        run((Plan) plan, state, level, pos.immutable());
    }

    public static void tick(TickSource source, BlockState state, ServerLevel level, BlockPos pos) {
        if (!CommonConfigs.BLOCK_GROWTHS.get()) {
            return;
        }
        Plan plan = tables[source.ordinal()].plans.get(state.getBlock());
        if (plan == EMPTY) {
            return;
        }
        if (state.getBlock() instanceof IConditionalGrowingBlock conditional && !conditional.canGrow(state)) {
            return;
        }
        run(plan, state, level, pos);
    }

    private static void run(Plan plan, BlockState state, ServerLevel level, BlockPos pos) {
        LazyBiome biome = new LazyBiome(level, pos);
        IBlockGrowth[] growths = plan.growths;
        boolean[] chained = plan.chained;
        int size = growths.length;
        boolean oneRoll = CoOConfig.immersiveweatheringOneChanceRoll;
        for (int i = 0; i < size; ) {
            if (!oneRoll || !chained[i]) {
                growths[i].tryGrowing(pos, state, level, biome);
                i++;
                continue;
            }
            int end = plan.runEnd[i];
            int hit = pick(plan.keep, i, end, level.random.nextFloat());
            if (hit < 0) {
                i = end;
                continue;
            }
            grow((ConfigurableBlockGrowth) growths[hit], pos, state, level, biome);
            i = hit + 1;
        }
    }

    public static int pick(double[] keep, int start, int end, float roll) {
        double limit = (1.0f - roll) * keep[start];
        for (int k = start; k < end; k++) {
            if (keep[k + 1] < limit) {
                return k;
            }
        }
        return -1;
    }

    public static void grow(ConfigurableBlockGrowth growth, BlockPos pos, BlockState self, ServerLevel level, Supplier<Holder<Biome>> biome) {
        ConfigurableBlockGrowthAccessor accessor = (ConfigurableBlockGrowthAccessor) growth;
        for (IPositionRuleTest test : growth.getPositionTests()) {
            if (!test.test(biome, pos, level)) {
                return;
            }
        }
        if (!PlatHelper.isAreaLoaded(level, pos, accessor.coo$getMaxRange())) {
            return;
        }
        Direction dir = accessor.coo$getGrowthForDirection().getRandomValue(level.random).orElse(Direction.UP);
        RandomSource seed = RandomSource.create(Mth.getSeed(pos));
        boolean targetSelf = growth.targetSelf();
        BlockPos targetPos = targetSelf ? pos : pos.relative(dir);
        BlockState target = level.getBlockState(targetPos);
        RuleTest predicate = growth.getTargetPredicate();
        if (!targetSelf && !predicate.test(target, seed)) {
            return;
        }
        if (targetSelf && predicate instanceof RandomBlockMatchTestAccessor random && seed.nextFloat() >= random.getProbability()) {
            return;
        }
        SimpleWeightedRandomList<BlockPair> list = accessor.coo$getBlockGrowths().get(dir);
        if (list == null) {
            return;
        }
        BlockPair toPlace = list.getRandomValue(level.random).orElse(null);
        if (toPlace == null || !toPlace.getFirst().canSurvive(level, targetPos)) {
            return;
        }
        BlockPos targetPos2 = null;
        BlockState target2 = null;
        boolean isDouble = toPlace.isDouble();
        if (isDouble) {
            targetPos2 = targetPos.relative(dir);
            target2 = level.getBlockState(targetPos2);
            if (!predicate.test(target2, RandomSource.create(Mth.getSeed(pos)))) {
                return;
            }
        }
        if (!growth.getAreaCondition().test(pos, level, growth)) {
            return;
        }
        boolean destroy = growth.destroyTarget();
        if (destroy) {
            level.destroyBlock(targetPos, true);
        }
        level.setBlockAndUpdate(targetPos, accessor.coo$getStateToPlace(toPlace.getFirst(), target, level, targetPos));
        if (isDouble) {
            if (destroy) {
                level.destroyBlock(targetPos2, true);
            }
            level.setBlockAndUpdate(targetPos2, accessor.coo$getStateToPlace(toPlace.getSecond(), target2, level, pos));
        }
    }

    public static void skyTick(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        Table[] tables = ImmersiveWeatheringGrowths.tables;
        Table clear = tables[TickSource.CLEAR_SKY.ordinal()];
        Table rain = tables[TickSource.RAIN.ordinal()];
        Table snow = tables[TickSource.SNOW.ordinal()];
        float chance = (float) randomTickSpeed / 48.0f;
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        boolean raining = level.isRaining();
        do {
            if (!(chance > level.random.nextFloat())) {
                continue;
            }
            BlockPos target = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, level.getBlockRandomPos(minX, 0, minZ, 15)).below();
            BlockState state = level.getBlockState(target);
            Block block = state.getBlock();
            TickSource source;
            if (raining) {
                if (rain.plans.get(block) == EMPTY && snow.plans.get(block) == EMPTY) {
                    continue;
                }
                source = level.getBiome(target).value().getPrecipitationAt(target) == Biome.Precipitation.SNOW ? TickSource.SNOW : TickSource.RAIN;
            } else {
                if (clear.plans.get(block) == EMPTY) {
                    continue;
                }
                source = TickSource.CLEAR_SKY;
            }
            tick(source, state, level, target);
        } while ((chance -= 1.0f) > 0.0f);
    }

    private record Table(Reference2ObjectOpenHashMap<Block, Plan> plans) {
    }

    private record Plan(IBlockGrowth[] growths, boolean[] chained, int[] runEnd, double[] keep) {
    }

    public static final class LazyBiome implements Supplier<Holder<Biome>> {
        private final Level level;
        private final BlockPos pos;
        private Holder<Biome> biome;

        public LazyBiome(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public Holder<Biome> get() {
            Holder<Biome> biome = this.biome;
            if (biome == null) {
                biome = this.level.getBiome(this.pos);
                this.biome = biome;
            }
            return biome;
        }
    }
}
