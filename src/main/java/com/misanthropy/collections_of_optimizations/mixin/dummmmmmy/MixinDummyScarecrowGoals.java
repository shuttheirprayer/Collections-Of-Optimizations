package com.misanthropy.collections_of_optimizations.mixin.dummmmmmy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ThrottledCanUseGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.mehvahdjukaar.dummmmmmy.common.ModEvents", remap = false)
public abstract class MixinDummyScarecrowGoals {

    @WrapOperation(
            method = "onEntityJoinWorld(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;m_25352_(ILnet/minecraft/world/entity/ai/goal/Goal;)V"
            ),
            remap = false,
            require = 0
    )
    private static void coo$throttleScarecrowGoal(GoalSelector selector, int priority, Goal goal, Operation<Void> original) {
        int interval = CoOConfig.dummmmmmyScarecrowScanInterval;
        if (interval <= 0 || goal == null || goal instanceof ThrottledCanUseGoal) {
            original.call(selector, priority, goal);
            return;
        }
        original.call(selector, priority, new ThrottledCanUseGoal(goal, interval));
    }
}
