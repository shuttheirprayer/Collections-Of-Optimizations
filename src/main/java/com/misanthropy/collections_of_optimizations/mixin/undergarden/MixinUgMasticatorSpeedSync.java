package com.misanthropy.collections_of_optimizations.mixin.undergarden;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "quek.undergarden.entity.boss.Masticator", remap = false)
public abstract class MixinUgMasticatorSpeedSync {

    @WrapOperation(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;m_22100_(D)V"
            ),
            remap = false,
            require = 0
    )
    private void coo$skipRedundantSpeedSync(AttributeInstance instance, double value, Operation<Void> original) {
        if (!CoOConfig.undergardenSkipRedundantMasticatorSpeedSync || instance == null) {
            original.call(instance, value);
            return;
        }
        if (Math.abs(instance.getBaseValue() - value) <= 1.0E-5D) {
            return;
        }
        original.call(instance, value);
    }
}
