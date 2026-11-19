package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class FwaFastQuads {
    public static volatile boolean enabled = true;
    public static volatile boolean cullBackFaces = true;
    public static long culled;
    public static long emitted;
    public static final int SOLID = 0, CUTOUT_MIPPED = 1, TRANSLUCENT = 2;
    private static final int STRIDE = 32;
    private static final long[] scratch = new long[3];
    private static final int[] capacity = new int[3];
    private static final int[] count = new int[3];
    private static VertexFormatDescription format;
    private static boolean shadersChecked;
    private static boolean shaders;

    private FwaFastQuads() {
    }

    public static boolean active() {
        if (!enabled) {
            return false;
        }
        if (!shadersChecked) {
            shaders = IrisState.shaderPackInUse();
            shadersChecked = true;
        }
        return !shaders;
    }

    private static RenderType type(int slot) {
        return slot == SOLID ? RenderType.solid() : slot == CUTOUT_MIPPED ? RenderType.cutoutMipped() : RenderType.translucentMovingBlock();
    }

    public static void emit(int slot, PoseStack.Pose pose, BakedQuad bakedQuad, float r, float g, float b, int light) {
        if (bakedQuad.getVertices().length < 32) {
            return;
        }
        int n = count[slot];
        if (n + 4 > capacity[slot]) {
            int grown = Math.max(256, capacity[slot] * 2);
            scratch[slot] = scratch[slot] == 0L ? MemoryUtil.nmemAlloc((long) grown * STRIDE) : MemoryUtil.nmemRealloc(scratch[slot], (long) grown * STRIDE);
            capacity[slot] = grown;
        }
        ModelQuadView quad = (ModelQuadView) bakedQuad;
        Matrix4f position = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        int tint = ColorABGR.pack(r, g, b, 1.0F);
        int face = MatrixHelper.transformNormal(normalMatrix, quad.getLightFace());
        float[] p = POSITIONS;
        for (int i = 0; i < 4; i++) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);
            p[i * 3] = MatrixHelper.transformPositionX(position, x, y, z);
            p[i * 3 + 1] = MatrixHelper.transformPositionY(position, x, y, z);
            p[i * 3 + 2] = MatrixHelper.transformPositionZ(position, x, y, z);
        }
        if (cullBackFaces && slot != TRANSLUCENT && facesAway(p, 0, 3, 6) && facesAway(p, 6, 9, 0)) {
            culled++;
            return;
        }
        long ptr = scratch[slot] + (long) n * STRIDE;
        for (int i = 0; i < 4; i++, ptr += STRIDE) {
            int packed = quad.getForgeNormal(i);
            MemoryUtil.memPutFloat(ptr, p[i * 3]);
            MemoryUtil.memPutFloat(ptr + 4, p[i * 3 + 1]);
            MemoryUtil.memPutFloat(ptr + 8, p[i * 3 + 2]);
            MemoryUtil.memPutInt(ptr + 12, ModelQuadUtil.mixARGBColors(quad.getColor(i), tint));
            MemoryUtil.memPutFloat(ptr + 16, quad.getTexU(i));
            MemoryUtil.memPutFloat(ptr + 20, quad.getTexV(i));
            MemoryUtil.memPutInt(ptr + 24, ModelQuadUtil.mergeBakedLight(quad.getLight(i), light));
            MemoryUtil.memPutInt(ptr + 28, (packed & 0xFFFFFF) == 0 ? face : MatrixHelper.transformNormal(normalMatrix, packed));
        }
        count[slot] = n + 4;
        emitted++;
        SpriteUtil.markSpriteActive(quad.getSprite());
    }

    private static final float[] POSITIONS = new float[12];
    private static final double BACK_MARGIN = 0.15;

    private static boolean facesAway(float[] p, int a, int b, int c) {
        double ux = p[b] - p[a], uy = p[b + 1] - p[a + 1], uz = p[b + 2] - p[a + 2];
        double vx = p[c] - p[a], vy = p[c + 1] - p[a + 1], vz = p[c + 2] - p[a + 2];
        double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
        return nx * p[a] + ny * p[a + 1] + nz * p[a + 2] > BACK_MARGIN * Math.sqrt(nx * nx + ny * ny + nz * nz);
    }

    public static void newFrame() {
        shadersChecked = false;
    }

    public static void flush(MultiBufferSource source) {
        for (int slot = 0; slot < 3; slot++) {
            int n = count[slot];
            if (n == 0) {
                continue;
            }
            count[slot] = 0;
            if (source == null) {
                continue;
            }
            VertexConsumer consumer = source.getBuffer(type(slot));
            VertexBufferWriter writer = VertexBufferWriter.tryOf(consumer);
            if (writer != null) {
                if (format == null) {
                    format = VertexFormatRegistry.instance().get(DefaultVertexFormat.BLOCK);
                }
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    writer.push(stack, scratch[slot], n, format);
                }
            } else {
                long ptr = scratch[slot];
                for (int i = 0; i < n; i++, ptr += STRIDE) {
                    int color = MemoryUtil.memGetInt(ptr + 12);
                    int light = MemoryUtil.memGetInt(ptr + 24);
                    int normal = MemoryUtil.memGetInt(ptr + 28);
                    consumer.vertex(MemoryUtil.memGetFloat(ptr), MemoryUtil.memGetFloat(ptr + 4), MemoryUtil.memGetFloat(ptr + 8),
                            (color & 0xFF) / 255.0F, (color >>> 8 & 0xFF) / 255.0F, (color >>> 16 & 0xFF) / 255.0F, (color >>> 24) / 255.0F,
                            MemoryUtil.memGetFloat(ptr + 16), MemoryUtil.memGetFloat(ptr + 20), 0, light,
                            (byte) normal / 127.0F, (byte) (normal >> 8) / 127.0F, (byte) (normal >> 16) / 127.0F);
                }
            }
        }
    }
}
