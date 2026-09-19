package com.misanthropy.collections_of_optimizations.mixin.blessfulled;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

@Pseudo
@Mixin(targets = "org.aqutheseal.blessfulled.integration.BSGeckoLib$FrostboundGeoLayer", remap = false)
public abstract class MixinBlessfulledGeoHurtOverlay {

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipInvisibleGeoHurtOverlay(PoseStack poseStack, GeoAnimatable animatable,
                                                 BakedGeoModel bakedModel, RenderType renderType,
                                                 MultiBufferSource bufferSource, VertexConsumer buffer,
                                                 float partialTick, int packedLight, int packedOverlay,
                                                 CallbackInfo ci) {
        if (!CoOConfig.blessfulledSkipInvisibleHurtOverlay) {
            return;
        }
        if (!(animatable instanceof LivingEntity living) || living.hurtTime <= 0) {
            ci.cancel();
        }
    }
}
