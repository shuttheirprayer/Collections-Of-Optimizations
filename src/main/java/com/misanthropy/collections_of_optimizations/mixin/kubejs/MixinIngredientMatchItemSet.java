package com.misanthropy.collections_of_optimizations.mixin.kubejs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Mixin(value = IngredientMatch.class, remap = false)
public abstract class MixinIngredientMatchItemSet {

    @Shadow
    public abstract ItemStack[] getAllItemArray();

    @Unique
    private Set<Item> coo$items;

    @Inject(method = "contains(Lnet/minecraft/world/item/crafting/Ingredient;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$itemSetLookup(Ingredient in, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.kubejsIngredientMatchItemSet || in == Ingredient.EMPTY || in.getClass() != Ingredient.class) {
            return;
        }
        Set<Item> items = coo$items;
        if (items == null) {
            items = Collections.newSetFromMap(new IdentityHashMap<>());
            for (ItemStack stack : getAllItemArray()) {
                items.add(stack.getItem());
            }
            coo$items = items;
        }
        for (ItemStack stack : in.getItems()) {
            if (items.contains(stack.getItem())) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }
}
