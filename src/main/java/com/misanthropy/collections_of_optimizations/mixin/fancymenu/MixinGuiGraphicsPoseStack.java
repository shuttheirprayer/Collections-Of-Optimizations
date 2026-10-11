package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.core.FancyMenuGuiPoseStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphicsPoseStack {

    @ModifyExpressionValue(
            method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
            at = @At(value = "NEW", target = "com/mojang/blaze3d/vertex/PoseStack")
    )
    private static PoseStack coo$trackedGuiPoseStack(PoseStack original) {
        return new FancyMenuGuiPoseStack();
    }
}
