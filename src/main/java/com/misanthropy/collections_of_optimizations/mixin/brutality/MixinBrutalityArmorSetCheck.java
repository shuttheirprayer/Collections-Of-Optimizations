package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.goo.brutality.util.ModUtils", remap = false)
public abstract class MixinBrutalityArmorSetCheck {

    @Inject(
            method = "hasFullArmorSet",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$rejectOnChestSlot(LivingEntity entity, ArmorMaterial material, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.brutalityFastArmorSetCheck || entity == null || material == null) {
            return;
        }
        Item item = entity.getItemBySlot(EquipmentSlot.CHEST).getItem();
        if (!(item instanceof ArmorItem armor) || armor.getMaterial() != material) {
            cir.setReturnValue(false);
        }
    }
}
