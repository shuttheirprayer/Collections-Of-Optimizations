package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.render.item.CMItemRenderProperties;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CMItemRenderProperties.class, remap = false)
public abstract class MixinCataclysmItemRenderer {

    @Unique
    private static BlockEntityWithoutLevelRenderer coo$shared;

    @WrapMethod(method = "getCustomRenderer", require = 0)
    private BlockEntityWithoutLevelRenderer coo$sharedRenderer(Operation<BlockEntityWithoutLevelRenderer> original) {
        if (!CoOConfig.cataclysmShareItemRenderer) {
            return original.call();
        }
        BlockEntityWithoutLevelRenderer shared = coo$shared;
        if (shared == null) {
            shared = original.call();
            if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager manager) {
                manager.registerReloadListener(shared);
            }
            coo$shared = shared;
        }
        return shared;
    }
}
