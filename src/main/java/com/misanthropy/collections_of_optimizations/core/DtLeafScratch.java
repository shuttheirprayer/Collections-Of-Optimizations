package com.misanthropy.collections_of_optimizations.core;

import com.ferreusveritas.dynamictrees.api.cell.Cell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class DtLeafScratch {

    public static final Direction[] DIRECTIONS = Direction.values();

    private static final int MAX_THICK_RADIUS = 32;

    private static final VoxelShape[] THICK_SHAPES = new VoxelShape[MAX_THICK_RADIUS + 1];

    private static final ThreadLocal<BlockPos.MutableBlockPos> HYDRATION_POS =
            ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    private static final ThreadLocal<Cell[]> HYDRATION_CELLS =
            ThreadLocal.withInitial(() -> new Cell[6]);

    private static final ThreadLocal<BlockPos.MutableBlockPos> PLACEMENT_POS =
            ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    private static final ThreadLocal<BlockPos.MutableBlockPos> LIGHT_POS =
            ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    static {
        for (int radius = 9; radius <= MAX_THICK_RADIUS; radius++) {
            double r = radius / 16.0D;
            THICK_SHAPES[radius] = Shapes.create(new AABB(0.5D - r, 0.0D, 0.5D - r, 0.5D + r, 1.0D, 0.5D + r));
        }
    }

    private DtLeafScratch() {
    }

    public static BlockPos.MutableBlockPos hydrationPos() {
        return HYDRATION_POS.get();
    }

    public static Cell[] hydrationCells() {
        return HYDRATION_CELLS.get();
    }

    public static BlockPos.MutableBlockPos placementPos() {
        return PLACEMENT_POS.get();
    }

    public static BlockPos.MutableBlockPos lightPos() {
        return LIGHT_POS.get();
    }

    public static VoxelShape thickTrunkShape(int radius) {
        return radius > 8 && radius <= MAX_THICK_RADIUS ? THICK_SHAPES[radius] : null;
    }
}
