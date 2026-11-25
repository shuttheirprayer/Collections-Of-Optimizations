package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Arrays;

public final class CubeVertices {
    private static final int CORNER = 12;

    private final float[] corners;
    private final int[] indices;
    private final float[] uvs;
    private final float[] normals;

    private CubeVertices(float[] corners, int[] indices, float[] uvs, float[] normals) {
        this.corners = corners;
        this.indices = indices;
        this.uvs = uvs;
        this.normals = normals;
    }

    public interface Holder {
        CubeVertices coo$cubeVertices();
    }

    public static CubeVertices capture(ModelPart.Cube cube) {
        CitadelModelVertices.Recorder recorder = new CitadelModelVertices.Recorder();
        cube.compile(CitadelModelVertices.IDENTITY, recorder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
        float[] data = recorder.data.toFloatArray();
        int count = data.length / 8;
        if ((count & 3) != 0) {
            throw new IllegalStateException("Not made of quads");
        }
        float[] corners = new float[count * 3];
        int[] indices = new int[count];
        float[] uvs = new float[count * 2];
        float[] normals = new float[count / 4 * 3];
        int used = 0;
        for (int i = 0; i < count; i++) {
            float x = data[i * 8];
            float y = data[i * 8 + 1];
            float z = data[i * 8 + 2];
            int at = 0;
            while (at < used && (corners[at * 3] != x || corners[at * 3 + 1] != y || corners[at * 3 + 2] != z)) {
                at++;
            }
            if (at == used) {
                corners[used * 3] = x;
                corners[used * 3 + 1] = y;
                corners[used * 3 + 2] = z;
                used++;
            }
            indices[i] = at;
            uvs[i * 2] = data[i * 8 + 3];
            uvs[i * 2 + 1] = data[i * 8 + 4];
            if ((i & 3) == 0) {
                System.arraycopy(data, i * 8 + 5, normals, i / 4 * 3, 3);
            }
        }
        return new CubeVertices(Arrays.copyOf(corners, used * 3), indices, uvs, normals);
    }

    public void emit(PoseStack.Pose pose, VertexBufferWriter writer, int light, int overlay, int color) {
        int count = this.indices.length;
        if (count == 0) {
            return;
        }
        Matrix4f position = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        float[] corners = this.corners;
        float[] uvs = this.uvs;
        float[] normals = this.normals;
        int[] indices = this.indices;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long points = stack.nmalloc(16, corners.length / 3 * CORNER);
            for (int i = 0, at = 0; i < corners.length; i += 3, at += CORNER) {
                float x = corners[i];
                float y = corners[i + 1];
                float z = corners[i + 2];
                MemoryUtil.memPutFloat(points + at, MatrixHelper.transformPositionX(position, x, y, z));
                MemoryUtil.memPutFloat(points + at + 4, MatrixHelper.transformPositionY(position, x, y, z));
                MemoryUtil.memPutFloat(points + at + 8, MatrixHelper.transformPositionZ(position, x, y, z));
            }
            long buffer = stack.nmalloc(64, count * ModelVertex.STRIDE);
            long ptr = buffer;
            int normal = 0;
            for (int i = 0; i < count; i++) {
                if ((i & 3) == 0) {
                    int n = i / 4 * 3;
                    normal = MatrixHelper.transformNormal(normalMatrix, normals[n], normals[n + 1], normals[n + 2]);
                }
                long point = points + (long) indices[i] * CORNER;
                ModelVertex.write(ptr, MemoryUtil.memGetFloat(point), MemoryUtil.memGetFloat(point + 4), MemoryUtil.memGetFloat(point + 8),
                        color, uvs[i * 2], uvs[i * 2 + 1], overlay, light, normal);
                ptr += ModelVertex.STRIDE;
            }
            try (MemoryStack frame = stack.push()) {
                writer.push(frame, buffer, count, ModelVertex.FORMAT);
            }
        }
    }
}
