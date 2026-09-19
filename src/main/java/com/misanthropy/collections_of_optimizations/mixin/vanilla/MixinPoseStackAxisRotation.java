package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** Reimplemented from Lomka 0.5.4 by Starlev (LGPL-3.0). */
@Mixin(PoseStack.class)
public abstract class MixinPoseStackAxisRotation {

    @WrapOperation(method = "mulPose", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix4f;"), require = 0)
    private Matrix4f coo$rotatePose(Matrix4f m, Quaternionfc q, Operation<Matrix4f> original) {
        int axis = coo$axis(q);
        if (axis < 0) {
            return original.call(m, q);
        }
        float v = coo$component(q, axis), w = q.w(), s = 2.0F * v * w, c = w * w - v * v;
        switch (axis) {
            case 0 -> {
                float a0 = m.m10(), a1 = m.m11(), a2 = m.m12(), a3 = m.m13(), b0 = m.m20(), b1 = m.m21(), b2 = m.m22(), b3 = m.m23();
                m.m10(c * a0 + s * b0).m11(c * a1 + s * b1).m12(c * a2 + s * b2).m13(c * a3 + s * b3);
                m.m20(c * b0 - s * a0).m21(c * b1 - s * a1).m22(c * b2 - s * a2).m23(c * b3 - s * a3);
            }
            case 1 -> {
                float a0 = m.m20(), a1 = m.m21(), a2 = m.m22(), a3 = m.m23(), b0 = m.m00(), b1 = m.m01(), b2 = m.m02(), b3 = m.m03();
                m.m20(c * a0 + s * b0).m21(c * a1 + s * b1).m22(c * a2 + s * b2).m23(c * a3 + s * b3);
                m.m00(c * b0 - s * a0).m01(c * b1 - s * a1).m02(c * b2 - s * a2).m03(c * b3 - s * a3);
            }
            default -> {
                float a0 = m.m00(), a1 = m.m01(), a2 = m.m02(), a3 = m.m03(), b0 = m.m10(), b1 = m.m11(), b2 = m.m12(), b3 = m.m13();
                m.m00(c * a0 + s * b0).m01(c * a1 + s * b1).m02(c * a2 + s * b2).m03(c * a3 + s * b3);
                m.m10(c * b0 - s * a0).m11(c * b1 - s * a1).m12(c * b2 - s * a2).m13(c * b3 - s * a3);
            }
        }
        return m;
    }

    @WrapOperation(method = "mulPose", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix3f;"), require = 0)
    private Matrix3f coo$rotateNormal(Matrix3f n, Quaternionfc q, Operation<Matrix3f> original) {
        int axis = coo$axis(q);
        if (axis < 0) {
            return original.call(n, q);
        }
        float v = coo$component(q, axis), w = q.w(), s = 2.0F * v * w, c = w * w - v * v;
        switch (axis) {
            case 0 -> {
                float a0 = n.m10, a1 = n.m11, a2 = n.m12, b0 = n.m20, b1 = n.m21, b2 = n.m22;
                n.m10 = c * a0 + s * b0; n.m11 = c * a1 + s * b1; n.m12 = c * a2 + s * b2;
                n.m20 = c * b0 - s * a0; n.m21 = c * b1 - s * a1; n.m22 = c * b2 - s * a2;
            }
            case 1 -> {
                float a0 = n.m20, a1 = n.m21, a2 = n.m22, b0 = n.m00, b1 = n.m01, b2 = n.m02;
                n.m20 = c * a0 + s * b0; n.m21 = c * a1 + s * b1; n.m22 = c * a2 + s * b2;
                n.m00 = c * b0 - s * a0; n.m01 = c * b1 - s * a1; n.m02 = c * b2 - s * a2;
            }
            default -> {
                float a0 = n.m00, a1 = n.m01, a2 = n.m02, b0 = n.m10, b1 = n.m11, b2 = n.m12;
                n.m00 = c * a0 + s * b0; n.m01 = c * a1 + s * b1; n.m02 = c * a2 + s * b2;
                n.m10 = c * b0 - s * a0; n.m11 = c * b1 - s * a1; n.m12 = c * b2 - s * a2;
            }
        }
        return n;
    }

    @Unique
    private static int coo$axis(Quaternionfc q) {
        if (!CoOConfig.vanillaAxisAlignedRotation) {
            return -1;
        }
        boolean x = q.x() != 0.0F, y = q.y() != 0.0F, z = q.z() != 0.0F;
        return !y && !z ? 0 : !x && !z ? 1 : !x && !y ? 2 : -1;
    }

    @Unique
    private static float coo$component(Quaternionfc q, int axis) {
        return axis == 0 ? q.x() : axis == 1 ? q.y() : q.z();
    }
}
