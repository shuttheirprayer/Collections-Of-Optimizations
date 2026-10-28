package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EdgeCulling;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class MixinEdgeCulling {
    private MixinEdgeCulling() {
    }

    @Mixin(GameRenderer.class)
    public abstract static class WorldFov {
        @Inject(method = "getFov", at = @At("RETURN"))
        private void coo$noteWorldFov(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
            if (useFovSetting) {
                EdgeCulling.noteWorldFov(cir.getReturnValueD());
            }
        }
    }

    @Mixin(LevelRenderer.class)
    public abstract static class Frame {
        @Inject(method = "renderLevel", at = @At("HEAD"))
        private void coo$beginEdgeCulling(PoseStack poseStack, float partialTick, long finishNanos, boolean renderBlockOutline,
                                          Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection,
                                          CallbackInfo ci) {
            EdgeCulling.beginFrame(camera);
        }

        @Inject(method = "renderLevel", at = @At("RETURN"))
        private void coo$endEdgeCulling(PoseStack poseStack, float partialTick, long finishNanos, boolean renderBlockOutline,
                                        Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection,
                                        CallbackInfo ci) {
            EdgeCulling.endFrame();
        }
    }

    @Mixin(EntityRenderDispatcher.class)
    public abstract static class Entities {
        @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
        private <E extends Entity> void coo$edgeCullEntity(E entity, Frustum frustum, double camX, double camY, double camZ,
                                                           CallbackInfoReturnable<Boolean> cir) {
            if (cir.getReturnValueZ() && EdgeCulling.hidesEntity(entity)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Mixin(BlockEntityRenderDispatcher.class)
    public abstract static class BlockEntities {
        @Shadow
        public abstract <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E blockEntity);

        @Inject(method = "render", at = @At("HEAD"), cancellable = true)
        private <E extends BlockEntity> void coo$edgeCullBlockEntity(E blockEntity, float partialTick, PoseStack poseStack,
                                                                     MultiBufferSource buffer, CallbackInfo ci) {
            double keep = EdgeCulling.blockEntityKeep(blockEntity.getBlockPos());
            if (keep < 1.0) {
                BlockEntityRenderer<E> renderer = getRenderer(blockEntity);
                if (renderer != null && EdgeCulling.beyond(blockEntity.getBlockPos(), keep, renderer.getViewDistance())) {
                    ci.cancel();
                }
            }
        }
    }
}
