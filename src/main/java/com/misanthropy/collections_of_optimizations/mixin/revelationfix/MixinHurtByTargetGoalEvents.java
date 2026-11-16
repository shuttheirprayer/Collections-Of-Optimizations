package com.misanthropy.collections_of_optimizations.mixin.revelationfix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HurtByTargetGoal.class, priority = 1500)
public abstract class MixinHurtByTargetGoalEvents extends TargetGoal {

    MixinHurtByTargetGoalEvents(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @TargetHandler(
            mixin = "com.mega.revelationfix.mixin.HurtByTargetGoalMixin",
            name = "canUse$head"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0,
            remap = false
    )
    private void coo$skipNonSpiderHurtByTargetEvent(CallbackInfoReturnable<Boolean> targetCir, CallbackInfo ci) {
        if (CoOConfig.revelationfixSkipNonSpiderHurtByTargetEvents && !(this.mob instanceof Spider)) {
            ci.cancel();
        }
    }

    @TargetHandler(
            mixin = "com.mega.revelationfix.mixin.HurtByTargetGoalMixin",
            name = "canUse$beforeIgnore"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0,
            remap = false
    )
    private void coo$skipNonSpiderHurtByTargetIgnoreEvent(CallbackInfoReturnable<Boolean> targetCir, CallbackInfo ci) {
        if (CoOConfig.revelationfixSkipNonSpiderHurtByTargetEvents && !(this.mob instanceof Spider)) {
            ci.cancel();
        }
    }

    @TargetHandler(
            mixin = "com.mega.revelationfix.mixin.HurtByTargetGoalMixin",
            name = "canUse$tail"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0,
            remap = false
    )
    private void coo$skipNonSpiderHurtByTargetTailEvent(CallbackInfoReturnable<Boolean> targetCir, CallbackInfo ci) {
        if (CoOConfig.revelationfixSkipNonSpiderHurtByTargetEvents && !(this.mob instanceof Spider)) {
            ci.cancel();
        }
    }
}
