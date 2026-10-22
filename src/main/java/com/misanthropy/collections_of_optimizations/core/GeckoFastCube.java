package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

public final class GeckoFastCube {

    private static final int MAX_VERTICES = 64;

    private static final ClassValue<Boolean> OWN_QUAD_WRITER = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return type.getMethod("createVerticesOfQuad", GeoQuad.class, Matrix4f.class, Vector3f.class, VertexConsumer.class,
                        int.class, int.class, float.class, float.class, float.class, float.class).getDeclaringClass() != GeoRenderer.class;
            } catch (NoSuchMethodException | LinkageError exception) {
                return true;
            }
        }
    };

    private static final Vector3f NORMAL = new Vector3f();

    private GeckoFastCube() {
    }

    public static boolean tryRender(Object renderer, GeoCube cube, Matrix4f pose, Matrix3f normalMatrix, VertexConsumer buffer,
                                    int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (OWN_QUAD_WRITER.get(renderer.getClass())) {
            return false;
        }
        GeoQuad[] quads = cube.quads();
        int vertexCount = 0;
        for (GeoQuad quad : quads) {
            if (quad != null) {
                if (quad.vertices().length != 4) {
                    return false;
                }
                vertexCount += 4;
            }
        }
        if (vertexCount == 0) {
            return true;
        }
        if (vertexCount > MAX_VERTICES) {
            return false;
        }
        VertexBufferWriter writer = VertexBufferWriter.tryOf(buffer);
        if (writer == null) {
            return false;
        }
        int color = ColorABGR.pack(red, green, blue, alpha);
        Vector3f normal = NORMAL;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long start = stack.nmalloc(64, vertexCount * ModelVertex.STRIDE);
            long ptr = start;
            for (GeoQuad quad : quads) {
                if (quad == null) {
                    continue;
                }
                normalMatrix.transform(normal.set(quad.normal()));
                RenderUtils.fixInvertedFlatCube(cube, normal);
                int packedNormal = NormI8.pack(normal);
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = vertex.position();
                    float x = position.x();
                    float y = position.y();
                    float z = position.z();
                    ModelVertex.write(ptr,
                            MatrixHelper.transformPositionX(pose, x, y, z),
                            MatrixHelper.transformPositionY(pose, x, y, z),
                            MatrixHelper.transformPositionZ(pose, x, y, z),
                            color, vertex.texU(), vertex.texV(), packedOverlay, packedLight, packedNormal);
                    ptr += ModelVertex.STRIDE;
                }
            }
            writer.push(stack, start, vertexCount, ModelVertex.FORMAT);
        }
        return true;
    }
}
