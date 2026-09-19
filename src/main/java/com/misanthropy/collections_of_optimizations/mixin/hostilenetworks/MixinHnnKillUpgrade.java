package com.misanthropy.collections_of_optimizations.mixin.hostilenetworks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.HnnBrokenModelProbe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.hostilenetworks.HostileEvents", remap = false)
public abstract class MixinHnnKillUpgrade {

    @WrapOperation(
            method = "updateModels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/items/ItemStackHandler;getStackInSlot(I)Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 0
    )
    private static ItemStack coo$skipUnresolvedModelOnKill(ItemStackHandler handler, int slot,
                                                           Operation<ItemStack> original) {
        ItemStack stack = original.call(handler, slot);
        if (!CoOConfig.hostilenetworksSkipBrokenModelOnKill) {
            return stack;
        }
        return HnnBrokenModelProbe.isUnresolved(stack) ? ItemStack.EMPTY : stack;
    }
}
