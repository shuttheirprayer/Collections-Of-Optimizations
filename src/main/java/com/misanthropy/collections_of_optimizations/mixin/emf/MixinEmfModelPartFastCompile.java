package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CubeVertices;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPart;

import java.util.List;

@Mixin(value = EMFModelPart.class, remap = false)
public abstract class MixinEmfModelPartFastCompile {

    @Inject(method = "m_104290_", at = @At("HEAD"), cancellable = true)
    private void coo$compileThroughWriter(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!CoOConfig.emfFastModelRender) {
            return;
        }
        VertexBufferWriter writer = VertexBufferWriter.tryOf(consumer);
        if (writer == null) {
            return;
        }
        ci.cancel();
        List<ModelPart.Cube> cubes = ((EmfModelPartCubes) this).coo$cubes();
        int color = ColorABGR.pack(red, green, blue, alpha);
        boolean prepared = false;
        for (int i = 0, count = cubes.size(); i < count; i++) {
            ModelPart.Cube cube = cubes.get(i);
            if (cube instanceof CubeVertices.Holder holder) {
                holder.coo$cubeVertices().emit(pose, writer, light, overlay, color);
                continue;
            }
            ModelCuboid cuboid = ((ModelCuboidAccessor) cube).embeddium$getSimpleCuboid();
            if (cuboid == null) {
                cube.compile(pose, consumer, light, overlay, red, green, blue, alpha);
                continue;
            }
            if (!prepared) {
                prepared = true;
                EntityRenderer.prepareNormals(pose);
            }
            EntityRenderer.renderCuboidFast(pose, writer, cuboid, light, overlay, color);
        }
    }
}
