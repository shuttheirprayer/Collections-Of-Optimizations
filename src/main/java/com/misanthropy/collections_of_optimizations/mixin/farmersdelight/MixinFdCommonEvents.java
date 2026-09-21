package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.event.CommonEvents;

@Mixin(value = CommonEvents.class, remap = false)
public abstract class MixinFdCommonEvents {

    @Inject(method = "onAnimalsJoinWorld", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipClientTemptGoals(EntityJoinLevelEvent event, CallbackInfo ci) {
        if (CoOConfig.farmersdelightSkipClientTemptGoals && event.getLevel().isClientSide) {
            ci.cancel();
        }
    }

    @Inject(method = "getTemptGoalPriority", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanTemptGoalPriority(Mob mob, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.farmersdelightLeanTemptGoalPriority) {
            return;
        }
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof TemptGoal) {
                cir.setReturnValue(wrapped.getPriority());
                return;
            }
        }
        cir.setReturnValue(-1);
    }
}
