package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.Map;

@Mixin(value = CurioInventoryCapability.Provider.class, remap = false)
public abstract class MixinCuriosProviderForeignCapability {

    @WrapOperation(
            method = "getCapability",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/theillusivec4/curios/api/CuriosApi;getEntitySlots(Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/Map;"
            ),
            require = 0
    )
    private Map<String, ISlotType> coo$skipForeignCapability(LivingEntity wearer, Operation<Map<String, ISlotType>> original,
                                                            @Local(argsOnly = true) Capability<?> capability) {
        if (CoOConfig.curiosSkipForeignCapabilityLookup && capability != CuriosCapability.INVENTORY) {
            return Map.of();
        }
        return original.call(wearer);
    }
}
