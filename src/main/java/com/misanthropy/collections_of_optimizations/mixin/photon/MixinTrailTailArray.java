package com.misanthropy.collections_of_optimizations.mixin.photon;

import com.lowdragmc.photon.client.gameobject.particle.TrailParticle;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = TrailParticle.TailArray.class, remap = false)
public abstract class MixinTrailTailArray {

    @SuppressWarnings("checkstyle:MemberName")
    @Shadow
    @Final
    private TrailParticle this$0;

    @Shadow
    private int size;
    @Shadow
    private float[] posX;
    @Shadow
    private float[] posY;
    @Shadow
    private float[] posZ;
    @Shadow
    private float[] colorR;
    @Shadow
    private float[] colorG;
    @Shadow
    private float[] colorB;
    @Shadow
    private float[] colorA;
    @Shadow
    private float[] width;
    @Shadow
    private float[] lifeTime;

    @Shadow
    public abstract void add(TrailParticle.Tail tail);

    @Shadow
    private void removeLast() {
        throw new AssertionError();
    }

    @Shadow
    private TrailParticle.Tail copyAsTail(int index) {
        throw new AssertionError();
    }

    @WrapMethod(method = "renderInternal", require = 0)
    private void coo$leanTrailVertices(VertexConsumer buffer, float partialTicks, Vector3f cameraPos,
                                       Vector4f color, int light, Operation<Void> original) {
        TrailParticle outer = this.this$0;

        if (!CoOConfig.photonLeanTrailVertices || outer == null || (Object) outer.getTails() != this) {
            original.call(buffer, partialTicks, cameraPos, color, light);
            return;
        }

        Vector3f headPos = outer.getHeadPosition(partialTicks);
        boolean pushHead = true;
        int tailSize = this.size;
        if (tailSize > 0) {
            int last = tailSize - 1;
            if (Float.floatToIntBits(this.posX[last]) == Float.floatToIntBits(headPos.x)
                    && Float.floatToIntBits(this.posY[last]) == Float.floatToIntBits(headPos.y)
                    && Float.floatToIntBits(this.posZ[last]) == Float.floatToIntBits(headPos.z)) {
                pushHead = false;
            }
        }

        if (pushHead) {
            TrailParticle.Tail headTail = new TrailParticle.Tail(headPos, 100);
            headTail.previous = tailSize > 1 ? copyAsTail(tailSize - 1) : null;
            headTail.updateData();
            add(headTail);
        }

        float camX = cameraPos.x;
        float camY = cameraPos.y;
        float camZ = cameraPos.z;

        boolean hasLastNormal = false;
        float lastNormalX = 0.0F;
        float lastNormalY = 0.0F;
        float lastNormalZ = 0.0F;
        boolean hasLastUp = false;
        float lastUpX = 0.0F;
        float lastUpY = 0.0F;
        float lastUpZ = 0.0F;

        for (int i = 0; i < this.size - 1; i++) {
            if (this.lifeTime[i] - partialTicks <= 0 || this.lifeTime[i + 1] - partialTicks <= 0) {
                continue;
            }

            float currX = this.posX[i];
            float currY = this.posY[i];
            float currZ = this.posZ[i];
            float vecX = this.posX[i + 1] - currX;
            float vecY = this.posY[i + 1] - currY;
            float vecZ = this.posZ[i + 1] - currZ;
            float toTailX = currX - camX;
            float toTailY = currY - camY;
            float toTailZ = currZ - camZ;

            float normalX = Math.fma(vecY, toTailZ, -vecZ * toTailY);
            float normalY = Math.fma(vecZ, toTailX, -vecX * toTailZ);
            float normalZ = Math.fma(vecX, toTailY, -vecY * toTailX);
            float invLength = Math.invsqrt(Math.fma(normalX, normalX, Math.fma(normalY, normalY, normalZ * normalZ)));
            normalX *= invLength;
            normalY *= invLength;
            normalZ *= invLength;

            if (!hasLastNormal) {
                lastNormalX = normalX;
                lastNormalY = normalY;
                lastNormalZ = normalZ;
                hasLastNormal = true;
            }

            float avgX = (lastNormalX + normalX) / 2.0F;
            float avgY = (lastNormalY + normalY) / 2.0F;
            float avgZ = (lastNormalZ + normalZ) / 2.0F;

            float w = this.width[i];
            float upX = currX + avgX * w - camX;
            float upY = currY + avgY * w - camY;
            float upZ = currZ + avgZ * w - camZ;
            float downX = currX + avgX * -w - camX;
            float downY = currY + avgY * -w - camY;
            float downZ = currZ + avgZ * -w - camZ;

            float ta = color.w() * this.colorA[i];
            float tr = color.x() * this.colorR[i];
            float tg = color.y() * this.colorG[i];
            float tb = color.z() * this.colorB[i];

            Vector4f uvs = outer.getUVs(i, this.size, partialTicks);
            float u0 = uvs.x();
            float v0 = uvs.y();
            float v1 = uvs.w();

            if (!hasLastUp) {
                coo$pushVertex(buffer, light, upX, upY, upZ, tr, tg, tb, ta, u0, v0);
                coo$pushVertex(buffer, light, upX, upY, upZ, tr, tg, tb, ta, u0, v0);
            }

            coo$pushVertex(buffer, light, upX, upY, upZ, tr, tg, tb, ta, u0, v0);
            coo$pushVertex(buffer, light, downX, downY, downZ, tr, tg, tb, ta, u0, v1);

            lastUpX = upX;
            lastUpY = upY;
            lastUpZ = upZ;
            hasLastUp = true;
            lastNormalX = normalX;
            lastNormalY = normalY;
            lastNormalZ = normalZ;
        }

        int headIndex = this.size - 1;
        if (headIndex > 0 && hasLastNormal) {
            float headX = this.posX[headIndex];
            float headY = this.posY[headIndex];
            float headZ = this.posZ[headIndex];
            float w = this.width[headIndex];

            float upX = headX + lastNormalX * w - camX;
            float upY = headY + lastNormalY * w - camY;
            float upZ = headZ + lastNormalZ * w - camZ;
            float downX = headX + lastNormalX * -w - camX;
            float downY = headY + lastNormalY * -w - camY;
            float downZ = headZ + lastNormalZ * -w - camZ;

            float ta = color.w() * this.colorA[headIndex];
            float tr = color.x() * this.colorR[headIndex];
            float tg = color.y() * this.colorG[headIndex];
            float tb = color.z() * this.colorB[headIndex];

            Vector4f uvs = outer.getUVs(headIndex - 1, this.size, partialTicks);
            float v0 = uvs.y();
            float u1 = uvs.z();
            float v1 = uvs.w();

            coo$pushVertex(buffer, light, upX, upY, upZ, tr, tg, tb, ta, u1, v0);
            coo$pushVertex(buffer, light, downX, downY, downZ, tr, tg, tb, ta, u1, v1);

            lastUpX = upX;
            lastUpY = upY;
            lastUpZ = upZ;
            hasLastUp = true;
        }

        if (hasLastUp) {
            coo$pushVertex(buffer, light, lastUpX, lastUpY, lastUpZ, 0, 0, 0, 0, 0, 0);
            coo$pushVertex(buffer, light, lastUpX, lastUpY, lastUpZ, 0, 0, 0, 0, 0, 0);
        }

        if (pushHead) {
            removeLast();
        }
    }

    @Unique
    private static void coo$pushVertex(VertexConsumer buffer, int light, float x, float y, float z,
                                       float r, float g, float b, float a, float u, float v) {
        buffer.vertex(x, y, z).uv(u, v).color(r, g, b, a).uv2(light).endVertex();
    }
}
