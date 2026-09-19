package com.misanthropy.collections_of_optimizations.mixin.cnc;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModEntityFilter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class MixinCncEntityDimensions {

    @WrapMethod(method = "refreshDimensions", require = 0)
    private void coo$skipUnchangedRefresh(Operation<Void> original) {
        if (CoOConfig.cncSkipRedundantDimensionRefresh) {
            Entity self = (Entity) (Object) this;
            if (ModEntityFilter.CRITTERS_N_CRAWLERS.matches(self)) {
                EntityDimensions wanted = self.getDimensions(self.getPose());
                if (wanted.width == self.getBbWidth() && wanted.height == self.getBbHeight()) {
                    return;
                }
            }
        }
        original.call();
    }
}
