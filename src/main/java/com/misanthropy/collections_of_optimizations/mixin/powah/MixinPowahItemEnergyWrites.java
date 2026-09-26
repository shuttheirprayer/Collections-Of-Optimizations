package com.misanthropy.collections_of_optimizations.mixin.powah;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import owmii.powah.lib.logistics.energy.Energy;

@Mixin(targets = "owmii.powah.lib.logistics.energy.Energy$Item", remap = false)
public abstract class MixinPowahItemEnergyWrites {

    @Inject(method = "receiveEnergy(JZ)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemReceive(long maxReceive, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        Energy coo$self = (Energy) (Object) this;
        long moved = Math.min(coo$self.getCapacity() - coo$self.getStored(), Math.min(coo$self.getMaxReceive(), maxReceive));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }

    @Inject(method = "extractEnergy(JZ)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemExtract(long maxExtract, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        Energy coo$self = (Energy) (Object) this;
        long moved = Math.min(coo$self.getStored(), Math.min(coo$self.getMaxExtract(), maxExtract));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }

    @Inject(method = "consume(J)J", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipNoopItemConsume(long amount, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipNoopItemEnergyWrites) {
            return;
        }
        Energy coo$self = (Energy) (Object) this;
        long moved = Math.min(coo$self.getStored(), Math.max(0L, amount));
        if (moved == 0L) {
            cir.setReturnValue(0L);
        }
    }
}
