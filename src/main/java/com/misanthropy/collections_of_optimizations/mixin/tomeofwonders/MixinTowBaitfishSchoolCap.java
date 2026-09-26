package com.misanthropy.collections_of_optimizations.mixin.tomeofwonders;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.platypushasnohat.tome_of_wonders.entities.Baitfish", remap = false)
public abstract class MixinTowBaitfishSchoolCap {

    @ModifyExpressionValue(
            method = "m_6518_",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/platypushasnohat/tome_of_wonders/entities/Baitfish;m_6031_()I"
            ),
            require = 0
    )
    private int coo$capSchoolSpawn(int original) {
        return Math.min(original, CoOConfig.tomeofwondersBaitfishSchoolCap);
    }
}
