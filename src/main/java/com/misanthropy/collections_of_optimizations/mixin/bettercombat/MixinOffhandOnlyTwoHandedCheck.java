package com.misanthropy.collections_of_optimizations.mixin.bettercombat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.WeaponAttributesCache;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Player.class, priority = 1500)
public abstract class MixinOffhandOnlyTwoHandedCheck {

    @TargetHandler(
            mixin = "net.bettercombat.mixin.PlayerEntityMixin",
            name = "getEquippedStack_Pre"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/bettercombat/logic/WeaponRegistry;getAttributes(Lnet/minecraft/world/item/ItemStack;)Lnet/bettercombat/api/WeaponAttributes;"),
            require = 0,
            remap = false
    )
    private WeaponAttributes coo$offhandOnly(ItemStack stack, @Local(argsOnly = true) EquipmentSlot slot) {
        if (slot != EquipmentSlot.OFFHAND && CoOConfig.bettercombatOffhandOnlyTwoHandedCheck) {
            return null;
        }
        if (stack != null && CoOConfig.bettercombatCacheWeaponAttributes) {
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains("weapon_attributes", Tag.TAG_STRING)) {
                Object cached = WeaponAttributesCache.lookup(stack.getItem());
                if (cached != null) {
                    return (WeaponAttributes) WeaponAttributesCache.unwrap(cached);
                }
            }
        }
        return WeaponRegistry.getAttributes(stack);
    }
}
