package com.misanthropy.collections_of_optimizations.mixin.titanium;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SidedInventoryComponent.class, remap = false)
public abstract class MixinTitaniumSidedInventory {

    @Inject(method = "isValidForAnySlot", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipEmptyTransferScan(IItemHandler dest, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.titaniumSkipEmptyTransferScan && stack.isEmpty()) {
            cir.setReturnValue(-1);
        }
    }
}
