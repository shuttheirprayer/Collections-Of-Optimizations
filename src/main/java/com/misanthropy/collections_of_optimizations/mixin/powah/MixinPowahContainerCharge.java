package com.misanthropy.collections_of_optimizations.mixin.powah;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "owmii.powah.forge.ForgeEnvHandler", remap = false)
public abstract class MixinPowahContainerCharge {

    @Inject(
            method = "chargeItemsInContainer(Lnet/minecraft/world/Container;JJ)J",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$leanContainerCharge(Container container, long maxPerSlot, long maxTotal, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahLeanContainerCharge) {
            return;
        }
        if (container == null) {
            return;
        }

        if (maxTotal <= 0L || maxPerSlot <= 0L) {
            cir.setReturnValue(0L);
            return;
        }

        long charged = 0L;
        int size = container.getContainerSize();
        for (int slot = 0; slot < size; slot++) {
            long room = maxTotal - charged;
            if (room <= 0L) {
                break;
            }
            ItemStack stack = container.getItem(slot);
            if (stack == null || stack.isEmpty()) {
                continue;
            }
            IEnergyStorage storage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
            if (storage == null) {
                continue;
            }
            long request = Math.min(maxPerSlot, room);
            if (request > (long) Integer.MAX_VALUE) {
                request = (long) Integer.MAX_VALUE;
            }
            charged += (long) storage.receiveEnergy((int) request, false);
        }

        if (charged > 0L) {
            container.setChanged();
        }
        cir.setReturnValue(charged);
    }
}
