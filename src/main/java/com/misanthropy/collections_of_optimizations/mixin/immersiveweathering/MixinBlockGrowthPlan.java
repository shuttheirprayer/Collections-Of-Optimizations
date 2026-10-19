package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringGrowthHolder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class MixinBlockGrowthPlan implements ImmersiveWeatheringGrowthHolder {

    @Unique
    private Object coo$growthPlan;

    @Override
    public Object coo$getGrowthPlan() {
        return this.coo$growthPlan;
    }

    @Override
    public void coo$setGrowthPlan(Object plan) {
        this.coo$growthPlan = plan;
    }
}
