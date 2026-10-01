package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.DensityGraphReuse;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RandomState.class)
public abstract class MixinRandomStateDensityPlan implements DensityGraphReuse.PlanHolder {

    @Unique
    private volatile DensityGraphReuse.Plan coo$densityPlan;

    @Override
    public DensityGraphReuse.Plan coo$densityPlan() {
        return this.coo$densityPlan;
    }

    @Override
    public void coo$setDensityPlan(DensityGraphReuse.Plan plan) {
        this.coo$densityPlan = plan;
    }
}
