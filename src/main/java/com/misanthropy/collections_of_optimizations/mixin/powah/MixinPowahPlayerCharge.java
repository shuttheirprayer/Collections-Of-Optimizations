package com.misanthropy.collections_of_optimizations.mixin.powah;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(targets = "owmii.powah.forge.ForgeEnvHandler", remap = false)
public abstract class MixinPowahPlayerCharge {

    @Inject(
            method = "chargeItemsInPlayerInv(Lnet/minecraft/world/entity/player/Player;JJLjava/util/function/Predicate;)J",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$skipIdlePlayerCharge(Player player, long maxPerSlot, long maxTotal, Predicate<ItemStack> allowStack, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipIdlePlayerCharge) {
            return;
        }
        if (maxTotal <= 0L || maxPerSlot <= 0L) {
            cir.setReturnValue(0L);
        }
    }
}
