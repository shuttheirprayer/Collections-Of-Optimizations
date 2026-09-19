package com.misanthropy.collections_of_optimizations.mixin.oculus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BannerRenderer.class)
public abstract class MixinBannerRenderer {

    @Inject(
            method = "renderPatterns(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/resources/model/Material;ZLjava/util/List;Z)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipBannerPatternsInShadowPass(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                                          int packedOverlay, ModelPart flagPart, Material baseMaterial, boolean isBanner,
                                                          List<Pair<Holder<BannerPattern>, DyeColor>> patterns, boolean glint,
                                                          CallbackInfo ci) {
        if (CoOConfig.oculusSkipBannerPatternsInShadowPass && IrisState.shadowPass()) {
            flagPart.render(poseStack, baseMaterial.buffer(bufferSource, RenderType::entitySolid, glint), packedLight, packedOverlay);
            ci.cancel();
        }
    }
}
