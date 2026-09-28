package com.misanthropy.collections_of_optimizations.mixin.bettercombat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 1500)
public abstract class MixinOffhandOnlyTwoHandedCheck {

    @TargetHandler(
            mixin = "net.bettercombat.mixin.PlayerEntityMixin",
            name = "getEquippedStack_Pre"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$offhandOnly(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir, Operation<Void> operation) {
        if (slot != EquipmentSlot.OFFHAND && CoOConfig.bettercombatOffhandOnlyTwoHandedCheck) {
            return;
        }
        operation.call(slot, cir);
    }
}
