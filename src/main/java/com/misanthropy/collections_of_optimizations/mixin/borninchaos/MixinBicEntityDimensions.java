package com.misanthropy.collections_of_optimizations.mixin.borninchaos;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModEntityFilter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class MixinBicEntityDimensions {

    @WrapMethod(method = "refreshDimensions", require = 0)
    private void coo$skipUnchangedRefresh(Operation<Void> original) {
        if (CoOConfig.borninchaosSkipRedundantDimensionRefresh) {
            Entity self = (Entity) (Object) this;
            if (ModEntityFilter.BORN_IN_CHAOS.matches(self)) {
                EntityDimensions wanted = self.getDimensions(self.getPose());
                if (wanted.width == self.getBbWidth() && wanted.height == self.getBbHeight()) {
                    return;
                }
            }
        }
        original.call();
    }
}
