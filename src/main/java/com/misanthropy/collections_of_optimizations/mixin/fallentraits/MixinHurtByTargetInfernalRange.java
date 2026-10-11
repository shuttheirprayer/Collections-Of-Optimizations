package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtInfernalRange;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HurtByTargetGoal.class)
public abstract class MixinHurtByTargetInfernalRange extends TargetGoal {

    protected MixinHurtByTargetInfernalRange(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @ModifyVariable(method = "alertOthers", at = @At(value = "STORE", ordinal = 0), ordinal = 0, require = 0)
    private double coo$clampInfernalAlertRange(double range) {
        return range > FtInfernalRange.CAP ? FtInfernalRange.clamp(this.mob, range) : range;
    }
}
