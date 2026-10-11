package com.misanthropy.collections_of_optimizations.mixin.saintsdragons;

import com.leon.saintsdragons.server.entity.base.RideableDragonBase;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SaintsDragonsSeatBones;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Pseudo
@Mixin(targets = "com.leon.saintsdragons.client.renderer.DragonGeoEntityRenderer", remap = false)
public abstract class MixinDragonGeoEntityRenderer {

    @Unique
    private Set<Object> coo$trackingEnabledModels;

    @Inject(
            method = "enableTrackingForBones",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipRedundantBoneTracking(BakedGeoModel model, CallbackInfo ci) {
        if (!CoOConfig.saintsdragonsSkipRedundantBoneTracking || model == null) {
            return;
        }
        Set<Object> seen = this.coo$trackingEnabledModels;
        if (seen == null) {
            seen = Collections.newSetFromMap(new IdentityHashMap<>());
            this.coo$trackingEnabledModels = seen;
        }
        if (!seen.add(model)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/leon/saintsdragons/server/entity/base/RideableDragonBase;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", remap = true),
            cancellable = true,
            require = 0
    )
    private void coo$skipNonSeatBoneCapture(PoseStack poseStack, RideableDragonBase animatable, GeoBone bone,
                                            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                            boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                            float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (CoOConfig.saintsdragonsSkipNonSeatBoneCapture && !SaintsDragonsSeatBones.isSeatBone(animatable, bone.getName())) {
            ci.cancel();
        }
    }
}
