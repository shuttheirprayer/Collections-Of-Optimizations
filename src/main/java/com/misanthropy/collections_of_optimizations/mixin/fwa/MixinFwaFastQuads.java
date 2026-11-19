package com.misanthropy.collections_of_optimizations.mixin.fwa;

import com.misanthropy.collections_of_optimizations.core.FwaFastQuads;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.madu59.fwa.rendering.AnimationBlockData;
import fr.madu59.fwa.rendering.AnimationRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimationRenderer.class, remap = false)
public abstract class MixinFwaFastQuads {
    @Shadow
    private static MultiBufferSource bufferSource;

    @Shadow
    private static ClientLevel level;

    @Inject(method = "renderQuad", at = @At("HEAD"), cancellable = true, remap = false)
    private static void coo$fastQuad(PoseStack.Pose pose, BakedQuad bakedQuad, float a, float r, float g, float b, int light, AnimationBlockData blockData, CallbackInfo ci) {
        if (a < 1.0F || bufferSource == null || !FwaFastQuads.active()) {
            return;
        }
        Direction dir = bakedQuad.getDirection();
        float shade = blockData.isShaded() ? AnimationRenderer.getShade(dir.getStepX(), dir.getStepY(), dir.getStepZ(), pose) : 1.0F;
        ChunkRenderTypeSet renderTypes = blockData.getRenderTypes();
        if (renderTypes == null) {
            renderTypes = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockData.getBlockState()).getRenderTypes(blockData.getBlockState(), level.random, ModelData.EMPTY);
        }
        int slot;
        if (renderTypes.contains(RenderType.translucent())) {
            slot = FwaFastQuads.TRANSLUCENT;
        } else if (!renderTypes.contains(RenderType.cutoutMipped()) && !renderTypes.contains(RenderType.cutout())) {
            slot = FwaFastQuads.SOLID;
        } else {
            slot = FwaFastQuads.CUTOUT_MIPPED;
        }
        FwaFastQuads.emit(slot, pose, bakedQuad, Mth.floor(r * shade * 255.0F) / 255.0F, Mth.floor(g * shade * 255.0F) / 255.0F, Mth.floor(b * shade * 255.0F) / 255.0F, light);
        ci.cancel();
    }

    @Inject(method = "renderBuffers", at = @At("HEAD"), remap = false)
    private static void coo$flush(CallbackInfo ci) {
        FwaFastQuads.flush(bufferSource);
    }
}
