package com.misanthropy.collections_of_optimizations.mixin.immersivearmors;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveArmorsOverlayGate;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "immersive_armors.client.OverlayRenderer", remap = false)
public abstract class MixinImmersiveArmorsOverlay {

    @Inject(
            method = "renderOverlay",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdleSteampunkOverlay(GuiGraphics graphics, CallbackInfo ci) {
        if (!CoOConfig.immersivearmorsLeanSteampunkOverlay) {
            return;
        }
        if (!ImmersiveArmorsOverlayGate.mayRender()) {
            ci.cancel();
        }
    }
}
