package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RemoveBlockGoal.class)
public interface RemoveBlockGoalAccessor {

    @Accessor("blockToRemove")
    Block coo$blockToRemove();
}
