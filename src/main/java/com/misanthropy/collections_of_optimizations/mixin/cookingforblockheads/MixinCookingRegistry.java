package com.misanthropy.collections_of_optimizations.mixin.cookingforblockheads;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CfbKitchenItemIndex;
import net.blay09.mods.cookingforblockheads.api.SourceItem;
import net.blay09.mods.cookingforblockheads.api.ToasterHandler;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = CookingRegistry.class, remap = false)
public abstract class MixinCookingRegistry {

    @Shadow
    @Final
    private static NonNullList<ItemStack> tools;

    @Shadow
    @Final
    private static Map<ItemStack, Integer> ovenFuelItems;

    @Shadow
    @Final
    private static Map<ItemStack, ItemStack> ovenRecipes;

    @Shadow
    @Final
    private static Map<ItemStack, ToasterHandler> toastHandlers;

    @Shadow
    @Final
    private static NonNullList<ItemStack> waterItems;

    @Shadow
    @Final
    private static NonNullList<ItemStack> milkItems;

    @Inject(method = "addToolItem", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeTool(ItemStack toolItem, CallbackInfo ci) {
        if (CoOConfig.cookingforblockheadsIdempotentCompatReload && coo$hasSameItem(tools, toolItem)) {
            ci.cancel();
        }
    }

    @Inject(method = "addWaterItem", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeWater(ItemStack waterItem, CallbackInfo ci) {
        if (CoOConfig.cookingforblockheadsIdempotentCompatReload && coo$hasSameStack(waterItems, waterItem)) {
            ci.cancel();
        }
    }

    @Inject(method = "addMilkItem", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeMilk(ItemStack milkItem, CallbackInfo ci) {
        if (CoOConfig.cookingforblockheadsIdempotentCompatReload && coo$hasSameStack(milkItems, milkItem)) {
            ci.cancel();
        }
    }

    @Inject(method = "addOvenFuel", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeOvenFuel(ItemStack itemStack, int fuelTime, CallbackInfo ci) {
        if (!CoOConfig.cookingforblockheadsIdempotentCompatReload) {
            return;
        }
        for (Map.Entry<ItemStack, Integer> entry : ovenFuelItems.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), itemStack)) {
                if (entry.getValue() == fuelTime) {
                    ci.cancel();
                }
                return;
            }
        }
    }

    @Inject(method = "addSmeltingItem", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeSmeltingItem(ItemStack source, ItemStack result, CallbackInfo ci) {
        if (!CoOConfig.cookingforblockheadsIdempotentCompatReload) {
            return;
        }
        for (Map.Entry<ItemStack, ItemStack> entry : ovenRecipes.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), source)) {
                if (ItemStack.isSameItemSameTags(entry.getValue(), result)) {
                    ci.cancel();
                }
                return;
            }
        }
    }

    @Inject(method = "addToasterHandler", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeToasterHandler(ItemStack itemStack, ToasterHandler toastHandler, CallbackInfo ci) {
        if (!CoOConfig.cookingforblockheadsIdempotentCompatReload) {
            return;
        }
        for (ItemStack key : toastHandlers.keySet()) {
            if (ItemStack.isSameItem(key, itemStack)) {
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "findAnyItemStack", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipAbsentItems(ItemStack checkStack, List<IKitchenItemProvider> inventories, boolean requireBucket,
                                            CallbackInfoReturnable<SourceItem> cir) {
        if (!CoOConfig.cookingforblockheadsIndexRecipeBookScan || checkStack.isEmpty()) {
            return;
        }
        if (CfbKitchenItemIndex.isAbsent(inventories, checkStack.getItem())) {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private static boolean coo$hasSameItem(List<ItemStack> list, ItemStack stack) {
        for (ItemStack entry : list) {
            if (ItemStack.isSameItem(entry, stack)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean coo$hasSameStack(List<ItemStack> list, ItemStack stack) {
        for (ItemStack entry : list) {
            if (ItemStack.isSameItemSameTags(entry, stack)) {
                return true;
            }
        }
        return false;
    }
}
