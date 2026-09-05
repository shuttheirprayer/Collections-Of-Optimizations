package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.core.ArmorModelBake;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayerBake {

    @WrapMethod(
            method = "getArmorModelHook(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/client/model/HumanoidModel;)Lnet/minecraft/client/model/Model;",
            remap = false,
            require = 0
    )
    private Model coo$markArmorModelBuild(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
                                          HumanoidModel<?> defaultModel, Operation<Model> original) {
        ArmorModelBake.enter();
        try {
            return original.call(entity, stack, slot, defaultModel);
        } finally {
            ArmorModelBake.exit();
        }
    }
}
