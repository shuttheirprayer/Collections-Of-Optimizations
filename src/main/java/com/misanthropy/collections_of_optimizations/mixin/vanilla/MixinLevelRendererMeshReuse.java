package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererMeshReuse {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void coo$meshReuseFrame(CallbackInfo ci) {
        EntityMeshReuse.beginFrame();
    }

    @WrapOperation(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void coo$meshReuse(EntityRenderDispatcher dispatcher, Entity entity, double x, double y, double z, float yaw, float partialTick,
                               PoseStack poseStack, MultiBufferSource buffers, int light, Operation<Void> original) {
        EntityMeshReuse.render(dispatcher, entity, x, y, z, yaw, partialTick, poseStack, buffers, light,
                (e, ex, ey, ez, eyaw, ept, eposeStack, ebuffers, elight) -> original.call(dispatcher, e, ex, ey, ez, eyaw, ept, eposeStack, ebuffers, elight));
    }
}
