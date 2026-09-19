package com.misanthropy.collections_of_optimizations.mixin.oddaccessories;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.oddaccessories.util.CuriosUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CuriosUtil.class, remap = false)
public abstract class MixinOddCuriosUtil {

    @WrapMethod(method = "isEquipped(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z")
    private static boolean coo$fastEquippedMiss(LivingEntity entity, Item item, Operation<Boolean> original) {
        if (CoOConfig.oddaccessoriesFastCurioMiss && !CurioPresenceCache.mayHaveEquipped(entity, item)) {
            return false;
        }
        return original.call(entity, item);
    }

    @WrapMethod(method = "getEquippedStack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/ItemStack;")
    private static ItemStack coo$fastEquippedStackMiss(LivingEntity entity, Item item, Operation<ItemStack> original) {
        if (CoOConfig.oddaccessoriesFastCurioMiss && !CurioPresenceCache.mayHaveEquipped(entity, item)) {
            return ItemStack.EMPTY;
        }
        return original.call(entity, item);
    }
}
