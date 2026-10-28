package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.LayerModelReplay;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.class)
public abstract class MixinRenderLayerCutoutReplay {
    @Inject(method = "renderColoredCutoutModel", at = @At("HEAD"), cancellable = true)
    private static <T extends LivingEntity> void coo$replay(EntityModel<T> model, ResourceLocation texture, PoseStack poseStack, MultiBufferSource buffer, int light, T entity, float red, float green, float blue, CallbackInfo ci) {
        if (LayerModelReplay.isActive() && LayerModelReplay.render(model, entity, poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), red, green, blue)) {
            ci.cancel();
        }
    }
}
