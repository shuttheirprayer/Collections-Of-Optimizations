package com.misanthropy.collections_of_optimizations.mixin.mekaweapons;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "meranha.mekaweapons.items.ItemMekaBow", remap = false)
public abstract class MixinMekaBowOffhandArrow {

    @WrapOperation(
            method = "customArrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;)Lnet/minecraft/world/entity/projectile/AbstractArrow;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;m_21205_()Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 0
    )
    private ItemStack coo$drawnBowStack(Player player, Operation<ItemStack> original) {
        if (CoOConfig.mekaweaponsBowOffhandArrowStats) {
            ItemStack using = player.getUseItem();
            if (!using.isEmpty() && using.getItem() == (Object) this) {
                return using;
            }
        }
        return original.call(player);
    }
}
