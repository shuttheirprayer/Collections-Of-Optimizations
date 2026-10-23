package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.vanilla.ClipContextFluidAccessor;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public final class LeanRay {

    private static final int BOX_SLOTS = 512;
    private static final Boxes[] BOXES = new Boxes[BOX_SLOTS];
    private static final ThreadLocal<LeanRay> PER_THREAD = ThreadLocal.withInitial(LeanRay::new);

    private static final int MEMO_SIZE = 64;
    private static final int DIRECT_COLLIDER = ClipContext.Block.COLLIDER.ordinal();

    private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    private final long[] memoPos = new long[MEMO_SIZE];
    private final BlockState[] memoState = new BlockState[MEMO_SIZE];
    private final VoxelShape[] memoShape = new VoxelShape[MEMO_SIZE];
    private final int[] memoMode = new int[MEMO_SIZE];
    private int memoDepth;
    private UUID memoOwner;
    private Entity memoContextEntity;
    private CollisionContext memoContext;
    private int memoCount;
    public double hitX;
    public double hitY;
    public double hitZ;
    public Direction direction;
    public boolean inside;
    public BlockState hitState;
    private int blockX;
    private int blockY;
    private int blockZ;
    private double shapeX;
    private double shapeY;
    private double shapeZ;
    private Direction shapeDirection;
    private boolean shapeInside;

    private LeanRay() {}

    public static LeanRay get() {
        return PER_THREAD.get();
    }

    public static boolean handles(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        return Double.compare(fromX, toX) != 0 || Double.compare(fromY, toY) != 0 || Double.compare(fromZ, toZ) != 0;
    }

    public boolean cast(BlockGetter level, @Nullable ClipContext clip, @Nullable CollisionContext collision, boolean exactDirection,
                        double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        double endX = Mth.lerp(-1.0E-7, toX, fromX);
        double endY = Mth.lerp(-1.0E-7, toY, fromY);
        double endZ = Mth.lerp(-1.0E-7, toZ, fromZ);
        double startX = Mth.lerp(-1.0E-7, fromX, toX);
        double startY = Mth.lerp(-1.0E-7, fromY, toY);
        double startZ = Mth.lerp(-1.0E-7, fromZ, toZ);
        int x = Mth.floor(startX);
        int y = Mth.floor(startY);
        int z = Mth.floor(startZ);
        if (block(level, clip, collision, exactDirection, fromX, fromY, fromZ, toX, toY, toZ, x, y, z)) return true;
        double spanX = endX - startX;
        double spanY = endY - startY;
        double spanZ = endZ - startZ;
        int signX = Mth.sign(spanX);
        int signY = Mth.sign(spanY);
        int signZ = Mth.sign(spanZ);
        double stepX = signX == 0 ? Double.MAX_VALUE : (double) signX / spanX;
        double stepY = signY == 0 ? Double.MAX_VALUE : (double) signY / spanY;
        double stepZ = signZ == 0 ? Double.MAX_VALUE : (double) signZ / spanZ;
        double nextX = stepX * (signX > 0 ? 1.0 - Mth.frac(startX) : Mth.frac(startX));
        double nextY = stepY * (signY > 0 ? 1.0 - Mth.frac(startY) : Mth.frac(startY));
        double nextZ = stepZ * (signZ > 0 ? 1.0 - Mth.frac(startZ) : Mth.frac(startZ));
        while (nextX <= 1.0 || nextY <= 1.0 || nextZ <= 1.0) {
            if (nextX < nextY) {
                if (nextX < nextZ) {
                    x += signX;
                    nextX += stepX;
                } else {
                    z += signZ;
                    nextZ += stepZ;
                }
            } else if (nextY < nextZ) {
                y += signY;
                nextY += stepY;
            } else {
                z += signZ;
                nextZ += stepZ;
            }
            if (block(level, clip, collision, exactDirection, fromX, fromY, fromZ, toX, toY, toZ, x, y, z)) return true;
        }
        return false;
    }

    public void beginMemo(UUID owner) {
        if (memoDepth++ == 0) {
            memoOwner = owner;
            memoCount = 0;
        }
    }

    public void endMemo() {
        if (--memoDepth > 0) return;
        java.util.Arrays.fill(memoState, 0, memoCount, null);
        java.util.Arrays.fill(memoShape, 0, memoCount, null);
        memoCount = 0;
        memoOwner = null;
        memoContextEntity = null;
        memoContext = null;
    }

    public CollisionContext contextFor(Entity entity) {
        if (memoDepth > 0 && memoContextEntity == entity) return memoContext;
        CollisionContext context = CollisionContext.of(entity);
        if (memoDepth > 0 && entity.getUUID().equals(memoOwner)) {
            memoContextEntity = entity;
            memoContext = context;
        }
        return context;
    }

    private boolean memoFor(@Nullable ClipContext clip, @Nullable CollisionContext collision) {
        if (memoDepth == 0) return false;
        CollisionContext context = clip != null ? ((ClipContextFluidAccessor) clip).coo$collisionContext() : collision;
        return context instanceof EntityCollisionContext entityContext
                && entityContext.getEntity() != null
                && entityContext.getEntity().getUUID().equals(memoOwner);
    }

    public BlockPos blockPos() {
        return new BlockPos(blockX, blockY, blockZ);
    }

    public BlockHitResult result() {
        return new BlockHitResult(new Vec3(hitX, hitY, hitZ), direction, new BlockPos(blockX, blockY, blockZ), inside);
    }

    private boolean block(BlockGetter level, @Nullable ClipContext clip, @Nullable CollisionContext collision, boolean exactDirection,
                          double fromX, double fromY, double fromZ, double toX, double toY, double toZ, int x, int y, int z) {
        cursor.set(x, y, z);
        BlockState state = null;
        VoxelShape shape = null;
        boolean memo = memoFor(clip, collision);
        long key = memo ? BlockPos.asLong(x, y, z) : 0L;
        int mode = memo ? (clip != null ? ((ClipContextFluidAccessor) clip).coo$blockMode().ordinal() : DIRECT_COLLIDER) : 0;
        if (memo) {
            for (int i = 0; i < memoCount; i++) {
                if (memoPos[i] == key && memoMode[i] == mode) {
                    state = memoState[i];
                    shape = memoShape[i];
                    break;
                }
            }
        }
        if (state == null) {
            state = level.getBlockState(cursor);
            shape = clip != null ? clip.getBlockShape(state, level, cursor) : state.getCollisionShape(level, cursor, collision);
            if (memo && memoCount < MEMO_SIZE) {
                memoPos[memoCount] = key;
                memoState[memoCount] = state;
                memoShape[memoCount] = shape;
                memoMode[memoCount] = mode;
                memoCount++;
            }
        }
        if (!clipShape(shape, fromX, fromY, fromZ, toX, toY, toZ, x, y, z)) return false;
        hitX = shapeX;
        hitY = shapeY;
        hitZ = shapeZ;
        hitState = state;
        direction = shapeDirection;
        inside = shapeInside;
        blockX = x;
        blockY = y;
        blockZ = z;
        if (exactDirection && clipShape(state.getInteractionShape(level, cursor), fromX, fromY, fromZ, toX, toY, toZ, x, y, z)) {
            double ix = shapeX - fromX;
            double iy = shapeY - fromY;
            double iz = shapeZ - fromZ;
            double hx = hitX - fromX;
            double hy = hitY - fromY;
            double hz = hitZ - fromZ;
            if (ix * ix + iy * iy + iz * iz < hx * hx + hy * hy + hz * hz) direction = shapeDirection;
        }
        return true;
    }

    private boolean clipShape(VoxelShape shape, double fromX, double fromY, double fromZ, double toX, double toY, double toZ, int x, int y, int z) {
        if (shape.isEmpty()) return false;
        double dx = toX - fromX;
        double dy = toY - fromY;
        double dz = toZ - fromZ;
        if (dx * dx + dy * dy + dz * dz < 1.0E-7) return false;
        double[] boxes = boxesOf(shape);
        double nudgedX = fromX + dx * 0.001;
        double nudgedY = fromY + dy * 0.001;
        double nudgedZ = fromZ + dz * 0.001;
        double localX = nudgedX - (double) x;
        double localY = nudgedY - (double) y;
        double localZ = nudgedZ - (double) z;
        for (int i = 0; i < boxes.length; i += 6) {
            if (boxes[i] <= localX && localX < boxes[i + 3]
                    && boxes[i + 1] <= localY && localY < boxes[i + 4]
                    && boxes[i + 2] <= localZ && localZ < boxes[i + 5]) {
                shapeX = nudgedX;
                shapeY = nudgedY;
                shapeZ = nudgedZ;
                shapeDirection = Direction.getNearest(dx, dy, dz).getOpposite();
                shapeInside = true;
                return true;
            }
        }
        nearest = 1.0;
        Direction found = null;
        for (int i = 0; i < boxes.length; i += 6) {
            double minX = boxes[i] + (double) x;
            double minY = boxes[i + 1] + (double) y;
            double minZ = boxes[i + 2] + (double) z;
            double maxX = boxes[i + 3] + (double) x;
            double maxY = boxes[i + 4] + (double) y;
            double maxZ = boxes[i + 5] + (double) z;
            if (dx > 1.0E-7) {
                found = face(found, dx, dy, dz, minX, minY, maxY, minZ, maxZ, Direction.WEST, fromX, fromY, fromZ);
            } else if (dx < -1.0E-7) {
                found = face(found, dx, dy, dz, maxX, minY, maxY, minZ, maxZ, Direction.EAST, fromX, fromY, fromZ);
            }
            if (dy > 1.0E-7) {
                found = face(found, dy, dz, dx, minY, minZ, maxZ, minX, maxX, Direction.DOWN, fromY, fromZ, fromX);
            } else if (dy < -1.0E-7) {
                found = face(found, dy, dz, dx, maxY, minZ, maxZ, minX, maxX, Direction.UP, fromY, fromZ, fromX);
            }
            if (dz > 1.0E-7) {
                found = face(found, dz, dx, dy, minZ, minX, maxX, minY, maxY, Direction.NORTH, fromZ, fromX, fromY);
            } else if (dz < -1.0E-7) {
                found = face(found, dz, dx, dy, maxZ, minX, maxX, minY, maxY, Direction.SOUTH, fromZ, fromX, fromY);
            }
        }
        if (found == null) return false;
        shapeX = fromX + nearest * dx;
        shapeY = fromY + nearest * dy;
        shapeZ = fromZ + nearest * dz;
        shapeDirection = found;
        shapeInside = false;
        return true;
    }

    private double nearest;

    private Direction face(@Nullable Direction previous, double along, double acrossA, double acrossB, double plane,
                           double minA, double maxA, double minB, double maxB, Direction facing,
                           double start, double startA, double startB) {
        double t = (plane - start) / along;
        double a = startA + t * acrossA;
        double b = startB + t * acrossB;
        if (0.0 < t && t < nearest && minA - 1.0E-7 < a && a < maxA + 1.0E-7 && minB - 1.0E-7 < b && b < maxB + 1.0E-7) {
            nearest = t;
            return facing;
        }
        return previous;
    }

    private static double[] boxesOf(VoxelShape shape) {
        int slot = System.identityHashCode(shape) & (BOX_SLOTS - 1);
        Boxes cached = BOXES[slot];
        if (cached != null && cached.shape == shape) return cached.boxes;
        double[][] collected = {new double[24]};
        int[] count = {0};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double[] into = collected[0];
            if (count[0] + 6 > into.length) collected[0] = into = java.util.Arrays.copyOf(into, into.length * 2);
            into[count[0]++] = Math.min(minX, maxX);
            into[count[0]++] = Math.min(minY, maxY);
            into[count[0]++] = Math.min(minZ, maxZ);
            into[count[0]++] = Math.max(minX, maxX);
            into[count[0]++] = Math.max(minY, maxY);
            into[count[0]++] = Math.max(minZ, maxZ);
        });
        double[] boxes = java.util.Arrays.copyOf(collected[0], count[0]);
        BOXES[slot] = new Boxes(shape, boxes);
        return boxes;
    }

    private record Boxes(VoxelShape shape, double[] boxes) {}
}
