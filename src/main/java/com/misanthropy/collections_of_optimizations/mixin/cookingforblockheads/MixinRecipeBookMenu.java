package com.misanthropy.collections_of_optimizations.mixin.cookingforblockheads;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.core.CfbKitchenItemIndex;
import net.blay09.mods.cookingforblockheads.menu.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RecipeBookMenu.class, remap = false)
public abstract class MixinRecipeBookMenu {

    @WrapMethod(method = "findAndSendItemList", require = 0)
    private void coo$indexedItemList(Operation<Void> original) {
        CfbKitchenItemIndex.enter();
        try {
            original.call();
        } finally {
            CfbKitchenItemIndex.exit();
        }
    }

    @WrapMethod(method = "findAndSendRecipes", require = 0)
    private void coo$indexedRecipes(ItemStack outputItem, boolean forceNoFilter, Operation<Void> original) {
        CfbKitchenItemIndex.enter();
        try {
            original.call(outputItem, forceNoFilter);
        } finally {
            CfbKitchenItemIndex.exit();
        }
    }
}
