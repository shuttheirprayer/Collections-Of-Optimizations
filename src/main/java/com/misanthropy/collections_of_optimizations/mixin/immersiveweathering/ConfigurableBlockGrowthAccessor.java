package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.ordana.immersive_weathering.data.block_growths.BlockPair;
import com.ordana.immersive_weathering.data.block_growths.growths.ConfigurableBlockGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = ConfigurableBlockGrowth.class, remap = false)
public interface ConfigurableBlockGrowthAccessor {

    @Accessor("maxRange")
    int coo$getMaxRange();

    @Accessor("growthForDirection")
    SimpleWeightedRandomList<Direction> coo$getGrowthForDirection();

    @Accessor("blockGrowths")
    Map<Direction, SimpleWeightedRandomList<BlockPair>> coo$getBlockGrowths();

    @Invoker("getStateToPlace")
    BlockState coo$getStateToPlace(BlockState toPlace, BlockState target, Level level, BlockPos pos);
}
