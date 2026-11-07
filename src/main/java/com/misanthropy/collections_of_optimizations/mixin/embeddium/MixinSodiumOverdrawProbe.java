package com.misanthropy.collections_of_optimizations.mixin.embeddium;

import com.misanthropy.collections_of_optimizations.core.OverdrawProbe;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class MixinSodiumOverdrawProbe {

    @Inject(method = "drawChunkLayer", at = @At("HEAD"), require = 0)
    private void coo$beginOverdrawProbe(RenderType layer, PoseStack poseStack, double x, double y, double z, CallbackInfo ci) {
        if (OverdrawProbe.ENABLED) {
            OverdrawProbe.begin(layer);
        }
    }

    @Inject(method = "drawChunkLayer", at = @At("RETURN"), require = 0)
    private void coo$endOverdrawProbe(RenderType layer, PoseStack poseStack, double x, double y, double z, CallbackInfo ci) {
        if (OverdrawProbe.ENABLED) {
            OverdrawProbe.end();
            if (layer == RenderType.solid()) {
                OverdrawProbe.frame();
            }
        }
    }
}
