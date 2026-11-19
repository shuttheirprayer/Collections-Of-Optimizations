package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class FwaLod {
    public static volatile boolean enabled = true;
    public static volatile int distance = 64;
    public static final int HYSTERESIS = 8;
    public static final int UNKNOWN = 0, ANIMATED = 1, BAKED = 2;
    private static final int SETTLE_FRAMES = 6;
    private static final int RETRY_FRAMES = 40;
    private static volatile double camX, camZ;
    private static volatile long frame;

    private FwaLod() {
    }

    public interface State {
        int coo$lodState();

        void coo$lodState(int state, long frame);

        long coo$lodChanged();

        long coo$lodRequested();

        void coo$lodRequested(long frame);
    }

    public static void beginFrame(Vec3 camera) {
        camX = camera.x;
        camZ = camera.z;
        frame++;
    }

    public static boolean far(BlockPos pos, int state) {
        double limit = distance + (state == ANIMATED ? HYSTERESIS : 0);
        double dx = pos.getX() + 0.5 - camX;
        double dz = pos.getZ() + 0.5 - camZ;
        return dx * dx + dz * dz > limit * limit;
    }

    public static boolean meshDecision(State animation, BlockPos pos) {
        boolean baked = far(pos, animation.coo$lodState());
        int next = baked ? BAKED : ANIMATED;
        if (animation.coo$lodState() != next) {
            animation.coo$lodState(next, frame);
        }
        return baked;
    }

    public static boolean skipRender(State animation, BlockPos pos, AABB box) {
        int state = animation.coo$lodState();
        if (state == UNKNOWN) {
            return false;
        }
        boolean wantBaked = far(pos, state);
        if (wantBaked != (state == BAKED) && frame - animation.coo$lodRequested() > RETRY_FRAMES) {
            animation.coo$lodRequested(frame);
            int sx = pos.getX() >> 4;
            int sz = pos.getZ() >> 4;
            int minY = ((int) Math.floor(box.minY)) >> 4;
            int maxY = ((int) Math.floor(box.maxY)) >> 4;
            for (int sy = minY; sy <= maxY; sy++) {
                Minecraft.getInstance().levelRenderer.setSectionDirty(sx, sy, sz);
            }
        }
        return state == BAKED && frame - animation.coo$lodChanged() > SETTLE_FRAMES;
    }
}
