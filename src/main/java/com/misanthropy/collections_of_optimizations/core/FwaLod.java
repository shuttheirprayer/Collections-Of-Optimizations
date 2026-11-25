package com.misanthropy.collections_of_optimizations.core;

import fr.madu59.fwa.Animations;
import fr.madu59.fwa.anims.Animation;
import fr.madu59.fwa.config.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class FwaLod {
    public static volatile boolean enabled = true;
    public static volatile int distance = 64;
    public static final int HYSTERESIS = 8;
    public static final int UNKNOWN = 0, ANIMATED = 1, BAKED = 2;
    private static final int SETTLE_FRAMES = 6;
    private static final int RETRY_FRAMES = 40;
    private static final int REBUILD_FRAMES = 20;
    private static final int MOVE = 8;
    private static volatile double camX, camY, camZ;
    private static volatile long frame;
    private static volatile boolean dirty = true;
    private static long builtFrame;
    private static double builtX, builtY, builtZ;
    private static final ArrayList<Map.Entry<Long, Animations.Bucket>> ACTIVE = new ArrayList<>();
    private static final Set<Map.Entry<Long, Animations.Bucket>> ACTIVE_VIEW = new AbstractSet<>() {
        @Override
        public Iterator<Map.Entry<Long, Animations.Bucket>> iterator() {
            return ACTIVE.iterator();
        }

        @Override
        public int size() {
            return ACTIVE.size();
        }
    };

    private FwaLod() {
    }

    public static void changed() {
        dirty = true;
    }

    public static Set<Map.Entry<Long, Animations.Bucket>> activeSections(Set<Map.Entry<Long, Animations.Bucket>> all) {
        double dx = camX - builtX;
        double dy = camY - builtY;
        double dz = camZ - builtZ;
        if (dirty || frame - builtFrame >= REBUILD_FRAMES || dx * dx + dy * dy + dz * dz > MOVE * MOVE) {
            dirty = false;
            builtFrame = frame;
            builtX = camX;
            builtY = camY;
            builtZ = camZ;
            ACTIVE.clear();
            for (Map.Entry<Long, Animations.Bucket> entry : all) {
                if (!idle(entry.getValue())) {
                    ACTIVE.add(entry);
                }
            }
        }
        return ACTIVE_VIEW;
    }

    private static boolean idle(Animations.Bucket bucket) {
        double limit = distance + HYSTERESIS + MOVE;
        double unseen = SettingsManager.INFINITE_ANIMATION_RENDER_DISTANCE.getValue()
                * Math.max(1.0F, SettingsManager.SHADOW_ANIMATION_RENDER_DISTANCE.getValue()) + MOVE;
        for (Animation animation : bucket.getAnimations()) {
            if (!animation.hasInfiniteAnimation() || animation.isForRemoval()) {
                return false;
            }
            BlockPos pos = animation.getPos();
            double dx = pos.getX() + 0.5 - camX;
            double dy = pos.getY() + 0.5 - camY;
            double dz = pos.getZ() + 0.5 - camZ;
            if (dx * dx + dy * dy + dz * dz > unseen * unseen) {
                continue;
            }
            State state = (State) animation;
            if (state.coo$lodState() != BAKED || frame - state.coo$lodChanged() <= SETTLE_FRAMES
                    || dx * dx + dz * dz <= limit * limit) {
                return false;
            }
        }
        return true;
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
        camY = camera.y;
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
