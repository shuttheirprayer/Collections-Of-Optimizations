package com.misanthropy.collections_of_optimizations.mixin.primitivemobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ThrottledCanUseGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.misanthropy.primitive_mobs.entity.passive.Sheepman", remap = false)
public abstract class MixinPmSheepmanPiglinAvoid {

    @WrapOperation(
            method = "m_8099_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;m_25352_(ILnet/minecraft/world/entity/ai/goal/Goal;)V"
            ),
            require = 0
    )
    private void coo$leanSheepmanPiglinAvoid(GoalSelector selector, int priority, Goal goal, Operation<Void> original) {
        int interval = CoOConfig.primitivemobsSheepmanPiglinAvoidInterval;
        if (interval <= 1 || !(goal instanceof AvoidEntityGoal)) {
            original.call(selector, priority, goal);
            return;
        }
        original.call(selector, priority, new ThrottledCanUseGoal(goal, interval));
    }
}
