package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GoalSelector.class)
public abstract class MixinGoalSelectorDisabledFlags {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;goalContainsAnyFlags(Lnet/minecraft/world/entity/ai/goal/WrappedGoal;Ljava/util/EnumSet;)Z"
            ),
            require = 0
    )
    private boolean coo$skipWhenNothingDisabled(WrappedGoal goal, EnumSet<Goal.Flag> disabled, Operation<Boolean> original) {
        if (CoOConfig.vanillaSkipEmptyDisabledFlags && disabled.isEmpty()) {
            return false;
        }
        return original.call(goal, disabled);
    }
}
