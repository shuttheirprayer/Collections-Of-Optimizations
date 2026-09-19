package com.misanthropy.collections_of_optimizations.mixin.cookingforblockheads;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.blay09.mods.cookingforblockheads.compat.json.JsonCompatLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JsonCompatLoader.class, remap = false)
public abstract class MixinJsonCompatLoader {

    @Inject(method = "m_6213_", at = @At("HEAD"), require = 0)
    private void coo$resetJsonNonFoodRecipes(ResourceManager resourceManager, CallbackInfo ci) {
        if (CoOConfig.cookingforblockheadsIdempotentCompatReload) {
            JsonCompatLoader.nonFoodRecipes.clear();
        }
    }
}
