package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.create.ArrayVoxelShapeUnionInvoker;
import com.misanthropy.collections_of_optimizations.mixin.create.VoxelShapeUnionAccessor;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ContraptionCollisionUnion {

    private static final double MIN_GAP = 1.0E-6D;

    private static final long MAX_CELLS = 1L << 27;

    private static final Direction.Axis[] AXES = {Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z};

    private ContraptionCollisionUnion() {
    }

    public static List<AABB> collisionBoxes(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks, BlockGetter level) {
        List<VoxelShape> shapes = new ArrayList<>();
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : blocks.entrySet()) {
            StructureTemplate.StructureBlockInfo info = entry.getValue();
            BlockPos pos = entry.getKey();
            VoxelShape shape = info.state().getCollisionShape(level, pos, CollisionContext.empty());
            if (!shape.isEmpty()) {
                shapes.add(shape.move(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
        return optimizedBoxes(combine(shapes));
    }

    public static List<AABB> optimizedBoxes(VoxelShape unoptimized) {
        Class<?> type = unoptimized.getClass();
        if (type != ArrayVoxelShape.class && type != CubeVoxelShape.class) {
            return unoptimized.optimize().toAabbs();
        }
        List<VoxelShape> parts = new ArrayList<>();
        unoptimized.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> parts.add(Shapes.box(minX, minY, minZ, maxX, maxY, maxZ)));
        return combine(parts).toAabbs();
    }

    public static VoxelShape combine(List<VoxelShape> shapes) {
        VoxelShape union;
        try {
            union = union(shapes);
        } catch (RuntimeException | LinkageError ignored) {
            union = null;
        }
        return union != null ? union : chain(shapes);
    }

    public static VoxelShape chain(List<VoxelShape> shapes) {
        VoxelShape combined = Shapes.empty();
        for (VoxelShape shape : shapes) {
            combined = Shapes.joinUnoptimized(combined, shape, BooleanOp.OR);
        }
        return combined;
    }

    private static VoxelShape union(List<VoxelShape> shapes) {
        int count = shapes.size();
        if (count < 2) {
            return null;
        }
        DiscreteVoxelShape[] parts = new DiscreteVoxelShape[count];
        double[][][] local = new double[3][count][];
        int[] totals = new int[3];
        for (int i = 0; i < count; i++) {
            VoxelShape shape = shapes.get(i);
            DiscreteVoxelShape part = voxels(shape);
            if (part == null) {
                return null;
            }
            parts[i] = part;
            for (int axis = 0; axis < 3; axis++) {
                double[] values = readCoords(coords(shape, AXES[axis]), part.getSize(AXES[axis]));
                if (values == null) {
                    return null;
                }
                local[axis][i] = values;
                totals[axis] += values.length;
            }
        }

        double[][] grid = new double[3][];
        for (int axis = 0; axis < 3; axis++) {
            grid[axis] = mergeAxis(local[axis], totals[axis]);
            if (grid[axis] == null) {
                return null;
            }
        }
        int sizeX = grid[0].length - 1;
        int sizeY = grid[1].length - 1;
        int sizeZ = grid[2].length - 1;
        if ((long) sizeX * sizeY * sizeZ > MAX_CELLS) {
            return null;
        }

        BitSetDiscreteVoxelShape bits = new BitSetDiscreteVoxelShape(sizeX, sizeY, sizeZ);
        for (int i = 0; i < count; i++) {
            int[] xs = indices(local[0][i], grid[0]);
            int[] ys = indices(local[1][i], grid[1]);
            int[] zs = indices(local[2][i], grid[2]);
            if (xs == null || ys == null || zs == null) {
                return null;
            }
            DiscreteVoxelShape part = parts[i];
            for (int x = 0; x < xs.length - 1; x++) {
                for (int y = 0; y < ys.length - 1; y++) {
                    for (int z = 0; z < zs.length - 1; z++) {
                        if (part.isFull(x, y, z)) {
                            fill(bits, xs[x], xs[x + 1], ys[y], ys[y + 1], zs[z], zs[z + 1]);
                        }
                    }
                }
            }
        }
        return create(bits, grid[0], grid[1], grid[2]);
    }

    private static double[] readCoords(DoubleList list, int cells) {
        if (list == null) {
            return null;
        }
        int size = list.size();
        if (size < 2 || size != cells + 1) {
            return null;
        }
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            double value = list.getDouble(i);
            if (!Double.isFinite(value) || Double.doubleToRawLongBits(value) == Long.MIN_VALUE) {
                return null;
            }
            if (i > 0 && !(value > values[i - 1])) {
                return null;
            }
            values[i] = value;
        }
        return values;
    }

    private static double[] mergeAxis(double[][] lists, int total) {
        double[] merged = new double[total];
        int offset = 0;
        for (double[] list : lists) {
            System.arraycopy(list, 0, merged, offset, list.length);
            offset += list.length;
        }
        Arrays.sort(merged);
        int unique = 0;
        for (double value : merged) {
            if (unique > 0 && value == merged[unique - 1]) {
                continue;
            }
            if (unique > 0 && !(value - merged[unique - 1] > MIN_GAP)) {
                return null;
            }
            merged[unique++] = value;
        }
        return Arrays.copyOf(merged, unique);
    }

    private static int[] indices(double[] values, double[] grid) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            int index = Arrays.binarySearch(grid, values[i]);
            if (index < 0) {
                return null;
            }
            result[i] = index;
        }
        return result;
    }

    private static void fill(BitSetDiscreteVoxelShape bits, int fromX, int toX, int fromY, int toY, int fromZ, int toZ) {
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                for (int z = fromZ; z < toZ; z++) {
                    bits.fill(x, y, z);
                }
            }
        }
    }

    private static DiscreteVoxelShape voxels(VoxelShape shape) {
        return ((VoxelShapeUnionAccessor) shape).coo$getShape();
    }

    private static DoubleList coords(VoxelShape shape, Direction.Axis axis) {
        return ((VoxelShapeUnionAccessor) shape).coo$getCoords(axis);
    }

    private static VoxelShape create(DiscreteVoxelShape bits, double[] xs, double[] ys, double[] zs) {
        return ArrayVoxelShapeUnionInvoker.coo$create(bits, xs, ys, zs);
    }
}
