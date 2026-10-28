package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.minecraft.client.model.EntityModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class LayerModelReplay implements VertexConsumer, VertexBufferWriter {
    private static final LayerModelReplay RECORDER = new LayerModelReplay();
    private static final Set<Class<?>> UNREPLAYABLE = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Matrix4f POSE = new Matrix4f();
    private static final Matrix3f NORMAL = new Matrix3f();

    private static boolean active;
    private static boolean recorded;
    private static EntityModel<?> model;
    private static Object entity;
    private static int light;
    private static int overlay;
    private static float red;
    private static float green;
    private static float blue;

    private long ptr;
    private int capacity;
    private int bytes;
    private int count;
    private VertexFormatDescription format;
    private boolean invalid;

    private LayerModelReplay() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void begin() {
        active = true;
        recorded = false;
        model = null;
        entity = null;
    }

    public static void end() {
        active = false;
        recorded = false;
        model = null;
        entity = null;
    }

    public static boolean render(EntityModel<?> m, Object e, PoseStack poseStack, VertexConsumer target, int l, int o, float r, float g, float b) {
        if (!active) {
            return false;
        }
        VertexBufferWriter writer = VertexBufferWriter.tryOf(target);
        if (writer == null || UNREPLAYABLE.contains(m.getClass())) {
            return false;
        }
        PoseStack.Pose last = poseStack.last();
        if (!recorded || model != m || entity != e || light != l || overlay != o || red != r || green != g || blue != b
                || !POSE.equals(last.pose()) || !NORMAL.equals(last.normal())) {
            LayerModelReplay rec = RECORDER;
            rec.bytes = 0;
            rec.count = 0;
            rec.format = null;
            rec.invalid = false;
            m.renderToBuffer(poseStack, rec, l, o, r, g, b, 1.0F);
            if (rec.invalid || rec.count == 0) {
                recorded = false;
                if (rec.invalid) {
                    UNREPLAYABLE.add(m.getClass());
                }
                return false;
            }
            recorded = true;
            model = m;
            entity = e;
            light = l;
            overlay = o;
            red = r;
            green = g;
            blue = b;
            POSE.set(last.pose());
            NORMAL.set(last.normal());
        }
        LayerModelReplay rec = RECORDER;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VertexBufferWriter.copyInto(writer, stack, rec.ptr, rec.count, rec.format);
        }
        return true;
    }

    @Override
    public void push(MemoryStack stack, long src, int n, VertexFormatDescription fmt) {
        if (format != null && format != fmt) {
            invalid = true;
            return;
        }
        format = fmt;
        int length = n * fmt.stride();
        if (bytes + length > capacity) {
            int next = Math.max(capacity * 2, bytes + length);
            next = Math.max(next, 16384);
            ptr = ptr == 0L ? MemoryUtil.nmemAlloc(next) : MemoryUtil.nmemRealloc(ptr, next);
            capacity = next;
        }
        MemoryUtil.memCopy(src, ptr + bytes, length);
        bytes += length;
        count += n;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        invalid = true;
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        invalid = true;
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        invalid = true;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        invalid = true;
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        invalid = true;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        invalid = true;
        return this;
    }

    @Override
    public void endVertex() {
        invalid = true;
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        invalid = true;
    }

    @Override
    public void unsetDefaultColor() {
    }
}
