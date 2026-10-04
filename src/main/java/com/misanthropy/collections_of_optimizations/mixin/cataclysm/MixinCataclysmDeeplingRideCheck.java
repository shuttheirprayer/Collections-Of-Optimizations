package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.Deepling.AbstractDeepling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.Deepling.AbstractDeepling$RidingCoralssus", remap = false)
public abstract class MixinCataclysmDeeplingRideCheck {

    @Shadow
    @Final
    private AbstractDeepling drowned;

    @WrapMethod(method = "m_8036_", require = 0)
    private boolean coo$cheapChecksFirst(Operation<Boolean> original) {
        if (CoOConfig.cataclysmDeeplingRideCheckFirst
                && (this.drowned.isPassenger() || this.drowned.getMoistness() <= 300)) {
            return false;
        }
        return original.call();
    }
}
