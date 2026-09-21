package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(value = StoveBlockEntity.class, remap = false)
public abstract class MixinFdStoveBlockEntity {

    @WrapOperation(
            method = "cookingTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lvectorwing/farmersdelight/common/block/entity/StoveBlockEntity;isStoveBlockedAbove()Z"
            ),
            require = 0)
    private static boolean coo$skipBlockedCheckWhenEmpty(StoveBlockEntity stove, Operation<Boolean> original) {
        if (CoOConfig.farmersdelightSkipEmptyStoveWork) {
            ItemStackHandler inventory = stove.getInventory();
            boolean empty = true;
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                return false;
            }
        }
        return original.call(stove);
    }
}
