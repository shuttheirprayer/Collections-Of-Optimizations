package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtInfernalRange;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class MixinNearestAttackableTargetInfernalRange extends TargetGoal {

    protected MixinNearestAttackableTargetInfernalRange(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @ModifyVariable(method = "getTargetSearchArea", at = @At("HEAD"), argsOnly = true, require = 0)
    private double coo$clampInfernalSearchArea(double range) {
        return range > FtInfernalRange.CAP ? FtInfernalRange.clamp(this.mob, range) : range;
    }
}
