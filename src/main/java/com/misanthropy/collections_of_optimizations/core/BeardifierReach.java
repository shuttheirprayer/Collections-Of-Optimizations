package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public final class BeardifierReach {

    public static final BeardifierReach NONE = new BeardifierReach(new Object[0], new Object[0]);

    private static final int MIN_TERMS = 8;
    private static final int COORD_LIMIT = 1 << 29;
    private static final int BURY_REACH = 5;
    private static final int BEARD_REACH = 11;
    private static final int KERNEL_LOW = -12;
    private static final int KERNEL_HIGH = 11;
    private static final int COLUMNS = 256;

    private final Object[] pieces;
    private final Object[] junctions;
    private final int[] pieceMinX;
    private final int[] pieceMaxX;
    private final int[] pieceMinY;
    private final int[] pieceMaxY;
    private final int[] pieceMinZ;
    private final int[] pieceMaxZ;
    private final int[] junctionX;
    private final int[] junctionY;
    private final int[] junctionZ;

    private final long[] columnKeys = new long[COLUMNS];
    private final int[][] columnPieces = new int[COLUMNS][];
    private final int[][] columnJunctions = new int[COLUMNS][];

    private final View pieceView = new View();
    private final View junctionView = new View();

    private int lastX = Integer.MIN_VALUE;
    private int lastY = Integer.MIN_VALUE;
    private int lastZ = Integer.MIN_VALUE;

    private BeardifierReach(Object[] pieces, Object[] junctions) {
        this.pieces = pieces;
        this.junctions = junctions;
        this.pieceMinX = new int[pieces.length];
        this.pieceMaxX = new int[pieces.length];
        this.pieceMinY = new int[pieces.length];
        this.pieceMaxY = new int[pieces.length];
        this.pieceMinZ = new int[pieces.length];
        this.pieceMaxZ = new int[pieces.length];
        this.junctionX = new int[junctions.length];
        this.junctionY = new int[junctions.length];
        this.junctionZ = new int[junctions.length];
        Arrays.fill(this.columnKeys, Long.MIN_VALUE);
    }

    public static BeardifierReach plan(Object beardifier, ObjectListIterator<?> pieceIterator, ObjectListIterator<?> junctionIterator) {
        if (beardifier.getClass() != Beardifier.class || pieceIterator == null || junctionIterator == null
                || pieceIterator.hasPrevious() || junctionIterator.hasPrevious()) {
            return NONE;
        }
        List<Object> pieces = new ArrayList<>();
        List<Object> junctions = new ArrayList<>();
        try {
            while (pieceIterator.hasNext()) {
                pieces.add(pieceIterator.next());
            }
            while (junctionIterator.hasNext()) {
                junctions.add(junctionIterator.next());
            }
        } finally {
            pieceIterator.back(Integer.MAX_VALUE);
            junctionIterator.back(Integer.MAX_VALUE);
        }
        if (pieces.size() + junctions.size() < MIN_TERMS) {
            return NONE;
        }
        BeardifierReach reach = new BeardifierReach(pieces.toArray(), junctions.toArray());
        for (int i = 0; i < reach.pieces.length; i++) {
            if (!(reach.pieces[i] instanceof Beardifier.Rigid rigid) || rigid.getClass() != Beardifier.Rigid.class) {
                return NONE;
            }
            BoundingBox box = rigid.box();
            TerrainAdjustment adjustment = rigid.terrainAdjustment();
            if (box == null || box.getClass() != BoundingBox.class || adjustment == null
                    || box.minX() < -COORD_LIMIT || box.maxX() > COORD_LIMIT
                    || box.minY() < -COORD_LIMIT || box.maxY() > COORD_LIMIT
                    || box.minZ() < -COORD_LIMIT || box.maxZ() > COORD_LIMIT
                    || rigid.groundLevelDelta() < -COORD_LIMIT || rigid.groundLevelDelta() > COORD_LIMIT) {
                return NONE;
            }
            int ground = box.minY() + rigid.groundLevelDelta();
            int xz;
            switch (adjustment) {
                case NONE -> {
                    reach.pieceMinY[i] = 1;
                    reach.pieceMaxY[i] = 0;
                    continue;
                }
                case BURY -> {
                    xz = BURY_REACH;
                    reach.pieceMinY[i] = ground - BEARD_REACH;
                    reach.pieceMaxY[i] = ground + BEARD_REACH;
                }
                case BEARD_THIN -> {
                    xz = BEARD_REACH;
                    reach.pieceMinY[i] = ground + KERNEL_LOW;
                    reach.pieceMaxY[i] = ground + KERNEL_HIGH;
                }
                case BEARD_BOX -> {
                    xz = BEARD_REACH;
                    reach.pieceMinY[i] = ground - BEARD_REACH;
                    reach.pieceMaxY[i] = box.maxY() + BEARD_REACH;
                }
                default -> {
                    return NONE;
                }
            }
            reach.pieceMinX[i] = box.minX() - xz;
            reach.pieceMaxX[i] = box.maxX() + xz;
            reach.pieceMinZ[i] = box.minZ() - xz;
            reach.pieceMaxZ[i] = box.maxZ() + xz;
        }
        for (int i = 0; i < reach.junctions.length; i++) {
            if (!(reach.junctions[i] instanceof JigsawJunction junction) || junction.getClass() != JigsawJunction.class) {
                return NONE;
            }
            reach.junctionX[i] = junction.getSourceX();
            reach.junctionY[i] = junction.getSourceGroundY();
            reach.junctionZ[i] = junction.getSourceZ();
        }
        return reach;
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectListIterator<T> pieces(int x, int y, int z) {
        this.select(x, y, z);
        return (ObjectListIterator<T>) this.pieceView;
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectListIterator<T> junctions(int x, int y, int z) {
        this.select(x, y, z);
        return (ObjectListIterator<T>) this.junctionView;
    }

    private void select(int x, int y, int z) {
        if (x == this.lastX && y == this.lastY && z == this.lastZ) {
            return;
        }
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        int column = (x & 15) | (z & 15) << 4;
        long key = (long) x << 32 | (z & 0xFFFFFFFFL);
        if (this.columnKeys[column] != key) {
            this.buildColumn(column, key, x, z);
        }
        View view = this.pieceView;
        view.reset(this.pieces);
        for (int piece : this.columnPieces[column]) {
            if (y >= this.pieceMinY[piece] && y <= this.pieceMaxY[piece]) {
                view.add(piece);
            }
        }
        view = this.junctionView;
        view.reset(this.junctions);
        for (int junction : this.columnJunctions[column]) {
            int dy = y - this.junctionY[junction];
            if (dy >= KERNEL_LOW && dy <= KERNEL_HIGH) {
                view.add(junction);
            }
        }
    }

    private void buildColumn(int column, long key, int x, int z) {
        int[] near = new int[this.pieces.length];
        int count = 0;
        for (int i = 0; i < this.pieces.length; i++) {
            if (this.pieceMinY[i] <= this.pieceMaxY[i]
                    && x >= this.pieceMinX[i] && x <= this.pieceMaxX[i]
                    && z >= this.pieceMinZ[i] && z <= this.pieceMaxZ[i]) {
                near[count++] = i;
            }
        }
        this.columnPieces[column] = Arrays.copyOf(near, count);
        near = new int[this.junctions.length];
        count = 0;
        for (int i = 0; i < this.junctions.length; i++) {
            int dx = x - this.junctionX[i];
            int dz = z - this.junctionZ[i];
            if (dx >= KERNEL_LOW && dx <= KERNEL_HIGH && dz >= KERNEL_LOW && dz <= KERNEL_HIGH) {
                near[count++] = i;
            }
        }
        this.columnJunctions[column] = Arrays.copyOf(near, count);
        this.columnKeys[column] = key;
    }

    private static final class View implements ObjectListIterator<Object> {

        private Object[] items = new Object[0];
        private int[] indices = new int[8];
        private int size;
        private int position;

        void reset(Object[] items) {
            this.items = items;
            this.size = 0;
            this.position = 0;
            if (this.indices.length < items.length) {
                this.indices = new int[items.length];
            }
        }

        void add(int index) {
            this.indices[this.size++] = index;
        }

        @Override
        public boolean hasNext() {
            return this.position < this.size;
        }

        @Override
        public Object next() {
            if (this.position >= this.size) {
                throw new NoSuchElementException();
            }
            return this.items[this.indices[this.position++]];
        }

        @Override
        public boolean hasPrevious() {
            return this.position > 0;
        }

        @Override
        public Object previous() {
            if (this.position <= 0) {
                throw new NoSuchElementException();
            }
            return this.items[this.indices[--this.position]];
        }

        @Override
        public int nextIndex() {
            return this.position;
        }

        @Override
        public int previousIndex() {
            return this.position - 1;
        }

        @Override
        public int back(int n) {
            int moved = Math.min(Math.max(n, 0), this.position);
            this.position -= moved;
            return moved;
        }

        @Override
        public int skip(int n) {
            int moved = Math.min(Math.max(n, 0), this.size - this.position);
            this.position += moved;
            return moved;
        }
    }
}
