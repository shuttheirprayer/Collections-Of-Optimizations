package com.misanthropy.collections_of_optimizations.mixin.l2archery;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2ArcheryUpgradeCache;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "dev.xkmc.l2archery.content.item.GenericBowItem", remap = false)
public abstract class MixinL2ArcheryBowUpgrades {

    @Inject(
            method = "getUpgrades(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$cachedUpgrades(ItemStack stack, CallbackInfoReturnable<List<Object>> cir) {
        if (!CoOConfig.l2archeryCacheBowUpgrades || stack == null) {
            return;
        }

        ListTag list = L2ArcheryUpgradeCache.upgradeList(stack);
        if (list == null) {
            if (L2ArcheryUpgradeCache.hasNoUpgradeTag(stack)) {
                cir.setReturnValue(new ArrayList<>());
            }
            return;
        }

        List<Object> cached = L2ArcheryUpgradeCache.get(list);
        if (cached != null) {
            cir.setReturnValue(new ArrayList<>(cached));
        }
    }

    @Inject(
            method = "getUpgrades(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeUpgrades(ItemStack stack, CallbackInfoReturnable<List<Object>> cir) {
        if (!CoOConfig.l2archeryCacheBowUpgrades || stack == null) {
            return;
        }

        ListTag list = L2ArcheryUpgradeCache.upgradeList(stack);
        if (list != null) {
            L2ArcheryUpgradeCache.put(list, cir.getReturnValue());
        }
    }
}
