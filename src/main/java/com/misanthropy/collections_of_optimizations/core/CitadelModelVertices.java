package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.function.BiConsumer;

public final class CitadelModelVertices {
    private static final int FLOATS_PER_VERTEX = 8;
    private static final int VERTICES_PER_PUSH = 256;
    private static final PoseStack.Pose IDENTITY = new PoseStack().last();

    private final float[] vertices;
    private final int vertexCount;
    private final int boxCount;
    private final Object lastBox;

    private CitadelModelVertices(float[] vertices, int boxCount, Object lastBox) {
        this.vertices = vertices;
        this.vertexCount = vertices.length / FLOATS_PER_VERTEX;
        this.boxCount = boxCount;
        this.lastBox = lastBox;
    }

    public boolean matches(ObjectList<?> boxes) {
        int size = boxes.size();
        return size == this.boxCount && (size == 0 || boxes.get(size - 1) == this.lastBox);
    }

    public static CitadelModelVertices capture(ObjectList<?> boxes, BiConsumer<PoseStack.Pose, VertexConsumer> renderer) {
        Recorder recorder = new Recorder();
        renderer.accept(IDENTITY, recorder);
        int size = boxes.size();
        return new CitadelModelVertices(recorder.data.toFloatArray(), size, size == 0 ? null : boxes.get(size - 1));
    }

    public static CitadelModelVertices empty(ObjectList<?> boxes) {
        int size = boxes.size();
        return new CitadelModelVertices(new float[0], size, size == 0 ? null : boxes.get(size - 1));
    }

    public boolean isDrawable() {
        return this.vertexCount > 0 && (this.vertexCount & 3) == 0;
    }

    public void emit(PoseStack.Pose pose, VertexBufferWriter writer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f position = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        int color = ColorABGR.pack(red, green, blue, alpha);
        float[] data = this.vertices;
        int total = this.vertexCount;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(64, VERTICES_PER_PUSH * ModelVertex.STRIDE);
            int vertex = 0;
            while (vertex < total) {
                int batch = Math.min(VERTICES_PER_PUSH, total - vertex);
                long ptr = buffer;
                int normal = 0;
                for (int i = 0; i < batch; i++, vertex++) {
                    int base = vertex * FLOATS_PER_VERTEX;
                    float x = data[base];
                    float y = data[base + 1];
                    float z = data[base + 2];
                    if ((vertex & 3) == 0) {
                        normal = MatrixHelper.transformNormal(normalMatrix, data[base + 5], data[base + 6], data[base + 7]);
                    }
                    ModelVertex.write(ptr,
                            MatrixHelper.transformPositionX(position, x, y, z),
                            MatrixHelper.transformPositionY(position, x, y, z),
                            MatrixHelper.transformPositionZ(position, x, y, z),
                            color, data[base + 3], data[base + 4], overlay, light, normal);
                    ptr += ModelVertex.STRIDE;
                }
                writer.push(stack, buffer, batch, ModelVertex.FORMAT);
            }
        }
    }

    private static final class Recorder implements VertexConsumer {
        private final FloatArrayList data = new FloatArrayList();

        @Override
        public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
            this.data.add(x);
            this.data.add(y);
            this.data.add(z);
            this.data.add(u);
            this.data.add(v);
            this.data.add(normalX);
            this.data.add(normalY);
            this.data.add(normalZ);
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void endVertex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    }
}
