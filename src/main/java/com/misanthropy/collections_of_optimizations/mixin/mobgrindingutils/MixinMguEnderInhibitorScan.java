package com.misanthropy.collections_of_optimizations.mixin.mobgrindingutils;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MguInhibitorScan;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "mob_grinding_utils.events.MGUEndermanInhibitEvent", remap = false)
public abstract class MixinMguEnderInhibitorScan {

    @Inject(
            method = "getIsInhibited(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$sectionFilteredInhibitorScan(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.mobgrindingutilsFastEnderInhibitorScan || entity == null) {
            return;
        }
        int result = MguInhibitorScan.scan(entity.level(), entity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
        if (result == MguInhibitorScan.PRESENT) {
            cir.setReturnValue(Boolean.TRUE);
        } else if (result == MguInhibitorScan.ABSENT) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
