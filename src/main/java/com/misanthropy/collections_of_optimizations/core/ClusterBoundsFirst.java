package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.zeta.world.generator.multichunk.ClusterShape;
import org.violetmoon.zeta.world.generator.multichunk.MultiChunkFeatureGenerator;

import java.util.Set;
import java.util.function.Consumer;

public final class ClusterBoundsFirst {
    private static final Set<String> READ_ONLY_CONTEXTS = Set.of(
            "org.violetmoon.quark.content.world.gen.BigStoneClusterGenerator$1",
            "org.violetmoon.quark.content.world.undergroundstyle.base.UndergroundStyleGenerator$Context"
    );

    private static final ClassValue<Boolean> READ_ONLY = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return READ_ONLY_CONTEXTS.contains(type.getName());
        }
    };

    private static final ClassValue<Boolean> CHUNK_LOOP = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return type.getMethod("forEachChunkBlock", LevelReader.class, BlockPos.class, int.class, int.class, Consumer.class)
                        .getDeclaringClass() == MultiChunkFeatureGenerator.class;
            } catch (NoSuchMethodException | LinkageError e) {
                return false;
            }
        }
    };

    private ClusterBoundsFirst() {
    }

    public static boolean skips(Object context, ClusterShape shape, BlockPos pos) {
        if (!READ_ONLY.get(context.getClass())) {
            return false;
        }
        BlockPos src = shape.src();
        Vec3 radius = shape.radius();
        double dx = (pos.getX() - src.getX()) / radius.x;
        double dy = (pos.getY() - src.getY()) / radius.y;
        double dz = (pos.getZ() - src.getZ()) / radius.z;
        return dx * dx + dy * dy + dz * dz > 1.0;
    }

    public static boolean missesChunk(Object generator, Object context, ClusterShape shape, BlockPos chunkCorner) {
        if (!READ_ONLY.get(context.getClass()) || !CHUNK_LOOP.get(generator.getClass())) {
            return false;
        }
        BlockPos src = shape.src();
        Vec3 radius = shape.radius();
        int x = Math.min(Math.max(src.getX(), chunkCorner.getX()), chunkCorner.getX() + 15);
        int z = Math.min(Math.max(src.getZ(), chunkCorner.getZ()), chunkCorner.getZ() + 15);
        double dx = (x - src.getX()) / radius.x;
        double dz = (z - src.getZ()) / radius.z;
        return dx * dx + dz * dz > 1.0;
    }
}
