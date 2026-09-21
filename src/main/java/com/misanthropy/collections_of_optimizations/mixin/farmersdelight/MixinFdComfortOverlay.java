package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.client.gui.ComfortHealthOverlay;

@Mixin(value = ComfortHealthOverlay.class, remap = false)
public abstract class MixinFdComfortOverlay {

    @Unique
    private static NamedGuiOverlay coo$playerHealthOverlay;

    @WrapOperation(
            method = "onRenderGuiOverlayPost",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/gui/overlay/GuiOverlayManager;findOverlay(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraftforge/client/gui/overlay/NamedGuiOverlay;"
            ),
            require = 0)
    private NamedGuiOverlay coo$cacheHealthOverlayLookup(ResourceLocation id, Operation<NamedGuiOverlay> original) {
        if (!CoOConfig.farmersdelightCacheOverlayLookup) {
            return original.call(id);
        }
        NamedGuiOverlay cached = coo$playerHealthOverlay;
        if (cached == null) {
            cached = original.call(id);
            coo$playerHealthOverlay = cached;
        }
        return cached;
    }
}
