package com.misanthropy.collections_of_optimizations.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class OutlineEdgeCache {

    private static final int MAX_SHAPES = 256;

    private static final Reference2ObjectOpenHashMap<VoxelShape, double[]> EDGES = new Reference2ObjectOpenHashMap<>(64);

    private static final DoubleArrayList SCRATCH = new DoubleArrayList(144);

    private static final Shapes.DoubleLineConsumer COLLECTOR = (x1, y1, z1, x2, y2, z2) -> {
        SCRATCH.add(x1);
        SCRATCH.add(y1);
        SCRATCH.add(z1);
        SCRATCH.add(x2);
        SCRATCH.add(y2);
        SCRATCH.add(z2);
    };

    private OutlineEdgeCache() {
    }

    public static double[] edgesOf(VoxelShape shape, Operation<Void> original) {
        double[] cached = EDGES.get(shape);
        if (cached != null) {
            return cached;
        }

        SCRATCH.clear();
        original.call(shape, COLLECTOR);

        double[] edges = SCRATCH.toDoubleArray();
        if (EDGES.size() >= MAX_SHAPES) {
            EDGES.clear();
        }
        EDGES.put(shape, edges);
        return edges;
    }
}
