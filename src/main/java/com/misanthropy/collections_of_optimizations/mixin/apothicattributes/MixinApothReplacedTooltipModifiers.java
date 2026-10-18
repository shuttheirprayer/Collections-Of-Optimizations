package com.misanthropy.collections_of_optimizations.mixin.apothicattributes;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class MixinApothReplacedTooltipModifiers {

    @WrapOperation(
            method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"),
            require = 0
    )
    private Multimap<Attribute, AttributeModifier> coo$skipReplacedModifiers(ItemStack stack, EquipmentSlot slot, Operation<Multimap<Attribute, AttributeModifier>> original) {
        if (CoOConfig.apothicattributesSkipReplacedTooltipModifiers) {
            return ImmutableMultimap.of();
        }
        return original.call(stack, slot);
    }
}
