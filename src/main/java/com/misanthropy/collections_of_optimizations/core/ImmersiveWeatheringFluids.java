package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.immersiveweathering.AdjacentBlocksAccessor;
import com.mojang.datafixers.util.Pair;
import com.ordana.immersive_weathering.blocks.mossy.Mossable;
import com.ordana.immersive_weathering.data.fluid_generators.IFluidGenerator;
import com.ordana.immersive_weathering.data.fluid_generators.OtherFluidGenerator;
import com.ordana.immersive_weathering.data.fluid_generators.SelfFluidGenerator;
import com.ordana.immersive_weathering.data.fluid_generators.builtin.BurnMossGenerator;
import com.ordana.immersive_weathering.data.position_tests.IPositionRuleTest;
import com.ordana.immersive_weathering.util.Weatherable;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;
import java.util.Optional;

public final class ImmersiveWeatheringFluids {

    private static final Direction[] DIRECTIONS = Direction.values();

    private ImmersiveWeatheringFluids() {
    }

    public static boolean handles(List<IFluidGenerator> generators) {
        for (IFluidGenerator generator : generators) {
            Class<?> type = generator.getClass();
            if (type != OtherFluidGenerator.class && type != SelfFluidGenerator.class && type != BurnMossGenerator.class) {
                return false;
            }
        }
        return true;
    }

    public static Optional<Pair<BlockPos, SoundEvent>> generate(List<Direction> flowDirections, BlockPos pos, Level level, List<IFluidGenerator> generators) {
        if (generators.isEmpty() || !PlatHelper.isAreaLoaded(level, pos, 3)) {
            return Optional.empty();
        }
        BlockState[] neighbors = new BlockState[6];
        ImmersiveWeatheringGrowths.LazyBiome biome = new ImmersiveWeatheringGrowths.LazyBiome(level, pos);
        for (IFluidGenerator generator : generators) {
            BlockPos placed;
            if (generator instanceof OtherFluidGenerator other) {
                placed = other(other, flowDirections, pos, level, neighbors, biome);
            } else if (generator instanceof SelfFluidGenerator self) {
                placed = self(self, flowDirections, pos, level, neighbors, biome);
            } else {
                placed = burnMoss(flowDirections, pos, level, neighbors);
            }
            if (placed != null) {
                return Optional.of(Pair.of(placed, generator.getSound()));
            }
        }
        return Optional.empty();
    }

    private static BlockState neighbor(BlockState[] neighbors, Level level, BlockPos pos, Direction direction) {
        int index = direction.ordinal();
        BlockState state = neighbors[index];
        if (state == null) {
            state = level.getBlockState(pos.relative(direction));
            neighbors[index] = state;
        }
        return state;
    }

    private static BlockPos other(OtherFluidGenerator generator, List<Direction> flowDirections, BlockPos pos, Level level,
                                  BlockState[] neighbors, ImmersiveWeatheringGrowths.LazyBiome biome) {
        RuleTest target = generator.getTarget();
        IPositionRuleTest extra = generator.getExtraCheck().orElse(null);
        for (int i = 0, size = flowDirections.size(); i < size; i++) {
            Direction direction = flowDirections.get(i);
            if (!target.test(neighbor(neighbors, level, pos, direction), level.random)) {
                continue;
            }
            BlockPos side = pos.relative(direction);
            if (extra != null && !extra.test(biome, side, level)) {
                continue;
            }
            level.setBlockAndUpdate(side, generator.getGrowth());
            return side;
        }
        return null;
    }

    private static BlockPos self(SelfFluidGenerator generator, List<Direction> flowDirections, BlockPos pos, Level level,
                                 BlockState[] neighbors, ImmersiveWeatheringGrowths.LazyBiome biome) {
        AdjacentBlocksAccessor adjacent = (AdjacentBlocksAccessor) generator.getAdjacentBlocksCondition();
        IPositionRuleTest extra = generator.getPositionTests().orElse(null);
        List<RuleTest> any = adjacent.coo$getAnyBlocks();
        for (int r = 0, rules = any.size(); r < rules; r++) {
            RuleTest rule = any.get(r);
            boolean found = false;
            for (Direction direction : DIRECTIONS) {
                if (rule.test(neighbor(neighbors, level, pos, direction), level.random)
                        && (extra == null || extra.test(biome, pos.relative(direction), level))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        List<RuleTest> sides = adjacent.coo$getSidesBlocks();
        for (int r = 0, rules = sides.size(); r < rules; r++) {
            RuleTest rule = sides.get(r);
            boolean found = false;
            for (int i = 0, size = flowDirections.size(); i < size; i++) {
                Direction direction = flowDirections.get(i);
                if (direction.getAxis().isHorizontal()
                        && rule.test(neighbor(neighbors, level, pos, direction), level.random)
                        && (extra == null || extra.test(biome, pos.relative(direction), level))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        RuleTest up = adjacent.coo$getUpBlock();
        if (up != null && fails(up, pos, level, neighbors, extra, biome, Direction.UP)) {
            return null;
        }
        RuleTest down = adjacent.coo$getDownBlock();
        if (down != null && fails(down, pos, level, neighbors, extra, biome, Direction.DOWN)) {
            return null;
        }
        level.setBlockAndUpdate(pos, generator.getGrowth());
        return pos;
    }

    private static boolean fails(RuleTest test, BlockPos pos, Level level, BlockState[] neighbors, IPositionRuleTest extra,
                                 ImmersiveWeatheringGrowths.LazyBiome biome, Direction direction) {
        if (!test.test(neighbor(neighbors, level, pos, direction), level.random)) {
            return true;
        }
        return extra != null && !extra.test(biome, pos.relative(direction), level);
    }

    private static BlockPos burnMoss(List<Direction> flowDirections, BlockPos pos, Level level, BlockState[] neighbors) {
        for (int i = 0, size = flowDirections.size(); i < size; i++) {
            Direction direction = flowDirections.get(i);
            BlockState state = neighbor(neighbors, level, pos, direction);
            if (Mossable.MOSS_LEVEL_DECREASES.get().get(state.getBlock()) == null) {
                continue;
            }
            BlockState unaffected = Mossable.getUnaffectedMossBlock(state);
            if (unaffected == state) {
                continue;
            }
            BlockPos side = pos.relative(direction);
            level.setBlockAndUpdate(side, unaffected.setValue(Weatherable.WEATHERABLE, Weatherable.WeatheringState.STABLE));
            return side;
        }
        return null;
    }
}
