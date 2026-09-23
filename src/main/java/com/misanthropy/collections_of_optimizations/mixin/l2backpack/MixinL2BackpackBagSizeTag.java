package com.misanthropy.collections_of_optimizations.mixin.l2backpack;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2BackpackBagCount;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.xkmc.l2backpack.content.bag.AbstractBag", remap = false)
public abstract class MixinL2BackpackBagSizeTag {

    @Inject(
            method = "getSizeFactor(Lnet/minecraft/world/item/ItemStack;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readSizeFactorWithoutTag(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.l2backpackLeanBagTagRead || stack == null) {
            return;
        }

        cir.setReturnValue(L2BackpackBagCount.sizeFactor(stack));
    }
}
