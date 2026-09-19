package com.misanthropy.collections_of_optimizations.mixin.titanium;

import com.hrznstudio.titanium.util.RecipeUtil;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TitaniumRecipeListCache;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.RecipeManagerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = RecipeUtil.class, remap = false)
public abstract class MixinTitaniumRecipeUtil {

    @Inject(method = "getRecipes", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$memoRecipeLists(Level world, RecipeType<?> recipeType, CallbackInfoReturnable<List<?>> cir) {
        if (!CoOConfig.titaniumMemoRecipeLists || world == null || recipeType == null) {
            return;
        }
        RecipeManager manager = world.getRecipeManager();
        if (manager == null) {
            return;
        }
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> root = ((RecipeManagerAccessor) (Object) manager).coo$recipes();
        if (root == null) {
            return;
        }
        Map<ResourceLocation, Recipe<?>> source = root.get(recipeType);
        if (source == null) {
            cir.setReturnValue(List.of());
            return;
        }
        cir.setReturnValue(TitaniumRecipeListCache.get(recipeType, source));
    }
}
