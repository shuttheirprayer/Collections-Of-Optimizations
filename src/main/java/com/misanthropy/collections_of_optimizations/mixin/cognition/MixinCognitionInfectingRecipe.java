package com.misanthropy.collections_of_optimizations.mixin.cognition;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CognitionInfectionCache;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cyanogen.experienceobelisk.recipe.InfectingRecipe", remap = false)
public abstract class MixinCognitionInfectingRecipe {

    @Inject(method = "getInfectedBlockState", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$readCachedInfection(Level level, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (!CoOConfig.cognitionCacheInfectingRecipes || level == null || state == null) {
            return;
        }
        RecipeManager manager = level.getRecipeManager();
        Object cached = CognitionInfectionCache.lookup(manager, state.getBlock());
        if (cached == null) {
            return;
        }
        cir.setReturnValue(CognitionInfectionCache.isMiss(cached) ? null : (BlockState) cached);
    }

    @Inject(method = "getInfectedBlockState", at = @At("RETURN"), require = 0)
    private static void coo$storeInfection(Level level, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (!CoOConfig.cognitionCacheInfectingRecipes || level == null || state == null) {
            return;
        }
        CognitionInfectionCache.store(level.getRecipeManager(), state.getBlock(), cir.getReturnValue());
    }
}
