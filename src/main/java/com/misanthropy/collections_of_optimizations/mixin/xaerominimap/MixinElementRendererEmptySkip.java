package com.misanthropy.collections_of_optimizations.mixin.xaerominimap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import java.util.IdentityHashMap;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.element.render.MinimapElementRendererHandler;

@Mixin(value = MinimapElementRendererHandler.class, remap = false)
public abstract class MixinElementRendererEmptySkip {

    @Unique
    private final IdentityHashMap<Object, int[]> coo$emptyStamp = new IdentityHashMap<>();

    @Inject(method = "renderForRenderer", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipEmpty(MinimapElementRenderer<?, ?> renderer, GuiGraphics guiGraphics, int elementIndex,
                               MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers, int indexLimit,
                               MinimapElementRenderInfo renderInfo, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.xaeroSkipEmptyElementRenderers) {
            return;
        }
        int[] state = this.coo$emptyStamp.get(renderer);
        if (state != null && state[0] == ClientTickStamp.current()) {
            cir.setReturnValue(elementIndex);
        }
    }

    @Inject(method = "renderForRenderer", at = @At("RETURN"), require = 0)
    private void coo$recordEmpty(MinimapElementRenderer<?, ?> renderer, GuiGraphics guiGraphics, int elementIndex,
                                 MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers, int indexLimit,
                                 MinimapElementRenderInfo renderInfo, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.xaeroSkipEmptyElementRenderers) {
            return;
        }
        if (cir.getReturnValue() == elementIndex) {
            this.coo$emptyStamp.computeIfAbsent(renderer, k -> new int[1])[0] = ClientTickStamp.current();
        } else {
            this.coo$emptyStamp.remove(renderer);
        }
    }
}
