package com.misanthropy.collections_of_optimizations.mixin.saintsdragons;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.leon.saintsdragons.forge.client.event.DragonUIEventHandler", remap = false)
public abstract class MixinSaintsDragonsHudOverlay {

    @Inject(
            method = "onRenderGuiOverlay",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$drawOncePerFrame(RenderGuiOverlayEvent.Post event, CallbackInfo ci) {
        if (CoOConfig.saintsdragonsDrawHudOncePerFrame && event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            ci.cancel();
        }
    }
}
