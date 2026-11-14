package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.OutlineOverlaySkip;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTarget.class)
public abstract class MixinRenderTargetOutlineSkip {

    @Shadow
    @Final
    private float[] clearChannels;

    @Inject(method = "clear", at = @At("RETURN"))
    private void coo$noteOutlineClear(boolean onOsx, CallbackInfo ci) {
        OutlineOverlaySkip.cleared((RenderTarget) (Object) this, clearChannels[3]);
    }

    @WrapOperation(
            method = "_blitToScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferUploader;draw(Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;)V"
            ),
            require = 0
    )
    private void coo$skipEmptyOutline(BufferBuilder.RenderedBuffer buffer, Operation<Void> original) {
        if (OutlineOverlaySkip.skipsDraw((RenderTarget) (Object) this)) {
            buffer.release();
            return;
        }
        original.call(buffer);
    }
}
