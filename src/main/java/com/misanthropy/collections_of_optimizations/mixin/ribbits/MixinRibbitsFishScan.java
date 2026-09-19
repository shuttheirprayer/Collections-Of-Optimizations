package com.misanthropy.collections_of_optimizations.mixin.ribbits;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "com.yungnickyoung.minecraft.ribbits.entity.goal.RibbitFishGoal", remap = false)
public abstract class MixinRibbitsFishScan {

    @Unique
    private int coo$fishScanCooldown;

    @WrapOperation(
            method = "m_8036_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;m_121930_(Lnet/minecraft/core/BlockPos;IILjava/util/function/Predicate;)Ljava/util/Optional;"
            ),
            require = 0
    )
    private Optional<BlockPos> coo$leanFishScan(BlockPos origin, int horizontalRange, int verticalRange, Predicate<BlockPos> filter, Operation<Optional<BlockPos>> original) {
        if (CoOConfig.ribbitsFishScanInterval > 1 && this.coo$fishScanCooldown > 0) {
            return Optional.empty();
        }
        return original.call(origin, horizontalRange, verticalRange, filter);
    }

    @Inject(method = "m_8036_", at = @At("RETURN"), require = 0)
    private void coo$armFishScan(CallbackInfoReturnable<Boolean> cir) {
        int interval = CoOConfig.ribbitsFishScanInterval;
        if (interval <= 1 || cir.getReturnValueZ()) {
            this.coo$fishScanCooldown = 0;
            return;
        }
        if (this.coo$fishScanCooldown > 0) {
            this.coo$fishScanCooldown--;
            return;
        }
        this.coo$fishScanCooldown = interval;
    }
}
