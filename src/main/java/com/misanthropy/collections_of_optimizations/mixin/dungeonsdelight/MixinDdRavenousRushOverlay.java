package com.misanthropy.collections_of_optimizations.mixin.dungeonsdelight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.yirmiri.dungeonsdelight.core.event.overlay.effect.RavenousRushEffectOverlay", remap = false)
public abstract class MixinDdRavenousRushOverlay {

    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipInvisibleOverlay(GuiGraphics graphics, ResourceLocation texture, float alpha, CallbackInfo ci) {
        if (CoOConfig.dungeonsdelightSkipInvisibleRushOverlay && alpha <= 0.0F) {
            ci.cancel();
        }
    }
}
