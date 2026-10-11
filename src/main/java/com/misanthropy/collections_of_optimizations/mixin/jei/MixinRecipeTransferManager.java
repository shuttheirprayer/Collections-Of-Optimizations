package com.misanthropy.collections_of_optimizations.mixin.jei;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import mezz.jei.library.recipes.RecipeTransferManager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(value = RecipeTransferManager.class, remap = false)
public abstract class MixinRecipeTransferManager {

    @Shadow
    @Final
    @Mutable
    private Set<AbstractContainerMenu> unsupportedContainers;

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void coo$weakUnsupportedContainers(CallbackInfo ci) {
        if (CoOConfig.jeiWeakUnsupportedContainers && unsupportedContainers.isEmpty()) {
            unsupportedContainers = Collections.newSetFromMap(new WeakHashMap<>());
        }
    }
}
