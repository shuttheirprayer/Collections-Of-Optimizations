package com.misanthropy.collections_of_optimizations.mixin.arsnspells;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.otectus.arsnspells.client.ManaBarController", remap = false)
public abstract class MixinAnsManaBarOverlay {

    @Inject(
            method = "onRenderOverlay",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipForeignOverlays(RenderGuiOverlayEvent.Pre event, CallbackInfo ci) {
        if (!CoOConfig.arsnspellsLeanManaBarOverlayGate) {
            return;
        }
        if (event.getOverlay() == null) {
            return;
        }
        ResourceLocation id = event.getOverlay().id();
        if (id == null) {
            return;
        }
        String namespace = id.getNamespace();
        if (!"irons_spellbooks".equals(namespace) && !"ars_nouveau".equals(namespace)) {
            ci.cancel();
        }
    }
}
