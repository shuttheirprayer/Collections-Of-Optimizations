package com.misanthropy.collections_of_optimizations.mixin.tact;

import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SubterranodonEntity.class, remap = false)
public abstract class MixinTactSubterranodonMeterClamp {

    @ModifyArg(
            method = "setMeterAmount(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"
            ),
            index = 1,
            remap = true,
            require = 0
    )
    private Object coo$clampSubterranodonMeter(Object value) {
        if (!CoOConfig.tactClampSubterranodonMeter) {
            return value;
        }
        if (value instanceof Float boxed) {
            float amount = boxed;
            if (amount < 0.0F) {
                return 0.0F;
            }
            if (amount > 1.0F) {
                return 1.0F;
            }
        }
        return value;
    }
}
