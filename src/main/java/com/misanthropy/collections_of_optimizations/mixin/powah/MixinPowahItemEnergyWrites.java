package com.misanthropy.collections_of_optimizations.mixin.powah;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "owmii.powah.lib.logistics.energy.Energy$Item", remap = false)
public abstract class MixinPowahItemEnergyWrites {

    @Shadow
    public abstract long getCapacity();

    @Shadow
    public abstract long getStored();

    @Shadow
    public abstract long getMaxReceive();

    @Shadow
    public abstract long getMaxExtract();

    @Inject(method = "receiveEnergy(JZ)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemReceive(long maxReceive, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        long moved = Math.min(this.getCapacity() - this.getStored(), Math.min(this.getMaxReceive(), maxReceive));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }

    @Inject(method = "extractEnergy(JZ)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemExtract(long maxExtract, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        long moved = Math.min(this.getStored(), Math.min(this.getMaxExtract(), maxExtract));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }

    @Inject(method = "consume(J)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemConsume(long amount, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        long moved = Math.min(this.getStored(), Math.max(0L, amount));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }
}
