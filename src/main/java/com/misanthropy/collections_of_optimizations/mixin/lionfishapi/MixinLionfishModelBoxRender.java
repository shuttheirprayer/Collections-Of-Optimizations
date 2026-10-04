package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CitadelModelVertices;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedModelBox.class, remap = false)
public abstract class MixinLionfishModelBoxRender {

    @Unique
    private static final Quaternionf coo$rotation = new Quaternionf();

    @Shadow
    public ObjectList<?> cubeList;

    @Unique
    private CitadelModelVertices coo$vertices;

    @Shadow
    private void doRender(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$renderThroughWriter(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!CoOConfig.lionfishapiFastModelRender) {
            return;
        }
        VertexBufferWriter writer = VertexBufferWriter.tryOf(consumer);
        if (writer == null) {
            return;
        }
        CitadelModelVertices vertices = this.coo$vertices;
        if (vertices == null || !vertices.matches(this.cubeList)) {
            try {
                vertices = CitadelModelVertices.capture(this.cubeList, (identity, recorder) -> this.doRender(identity, recorder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F));
            } catch (RuntimeException exception) {
                vertices = CitadelModelVertices.empty(this.cubeList);
            }
            this.coo$vertices = vertices;
        }
        if (!vertices.isDrawable()) {
            return;
        }
        vertices.emit(pose, writer, light, overlay, red, green, blue, alpha);
        ci.cancel();
    }

    @Redirect(
            method = "translateAndRotate",
            at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;m_252961_(F)Lorg/joml/Quaternionf;"),
            require = 0
    )
    private Quaternionf coo$reuseRotation(Axis axis, float angle) {
        if (CoOConfig.lionfishapiReuseBoxRotations) {
            if (axis == Axis.ZP) {
                return coo$rotation.rotationZ(angle);
            }
            if (axis == Axis.YP) {
                return coo$rotation.rotationY(angle);
            }
            if (axis == Axis.XP) {
                return coo$rotation.rotationX(angle);
            }
        }
        return axis.rotation(angle);
    }
}
