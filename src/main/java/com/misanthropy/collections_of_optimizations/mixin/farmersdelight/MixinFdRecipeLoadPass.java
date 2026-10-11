package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.core.FdToolActionPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class MixinFdRecipeLoadPass {

    @WrapMethod(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            require = 0
    )
    private void coo$toolActionScanPass(Map<ResourceLocation, JsonElement> recipes,
                                        ResourceManager resources,
                                        ProfilerFiller profiler,
                                        Operation<Void> original) {
        FdToolActionPass.enter();
        try {
            original.call(recipes, resources, profiler);
        } finally {
            FdToolActionPass.leave();
        }
    }
}
