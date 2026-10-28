package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class EdgeCulling {
    private static final double CENTER_SHARE = 0.6;
    private static final double EDGE_KEEP = 0.6;
    private static final double MIN_DISTANCE = 32.0;
    private static final double MIN_DISTANCE_SQ = MIN_DISTANCE * MIN_DISTANCE;

    private static boolean active;
    private static double camX;
    private static double camY;
    private static double camZ;
    private static double fwdX;
    private static double fwdZ;
    private static double centerCos;
    private static double centerAngle;
    private static double invRamp;
    private static double worldFov = -1.0;

    private EdgeCulling() {
    }

    public static void noteWorldFov(double fov) {
        worldFov = fov;
    }

    public static void beginFrame(Camera camera) {
        active = false;
        if (!CoOConfig.vanillaEdgeCulling) {
            return;
        }
        Vector3f look = camera.getLookVector();
        double horizontal = Math.sqrt((double) look.x() * look.x() + (double) look.z() * look.z());
        if (horizontal < 0.2) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double fov = worldFov;
        if (fov < 1.0 || fov > 179.0 || window.getHeight() <= 0) {
            return;
        }
        double aspect = (double) window.getWidth() / window.getHeight();
        double halfHorizontal = Math.atan(Math.tan(Math.toRadians(fov / 2.0)) * aspect);
        centerAngle = halfHorizontal * CENTER_SHARE;
        centerCos = Math.cos(centerAngle);
        invRamp = 1.0 / Math.max(1.0E-4, halfHorizontal - centerAngle);
        Vec3 position = camera.getPosition();
        camX = position.x;
        camY = position.y;
        camZ = position.z;
        fwdX = look.x() / horizontal;
        fwdZ = look.z() / horizontal;
        active = true;
    }

    public static void endFrame() {
        active = false;
    }

    public static boolean hidesEntity(Entity entity) {
        if (!active || IrisState.shadowPass()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (entity == mc.getCameraEntity() || entity.hasIndirectPassenger(mc.player) || entity.isCurrentlyGlowing()) {
            return false;
        }
        double dx = entity.getX() - camX;
        double dy = entity.getY() - camY;
        double dz = entity.getZ() - camZ;
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq < MIN_DISTANCE_SQ) {
            return false;
        }
        double keep = keep(dx, dz);
        return keep < 1.0 && !entity.shouldRenderAtSqrDistance(distSq / (keep * keep));
    }

    public static double blockEntityKeep(BlockPos pos) {
        if (!active) {
            return 1.0;
        }
        double dx = pos.getX() + 0.5 - camX;
        double dz = pos.getZ() + 0.5 - camZ;
        if (dx * dx + dz * dz < MIN_DISTANCE_SQ) {
            return 1.0;
        }
        double keep = keep(dx, dz);
        return keep < 1.0 && IrisState.shadowPass() ? 1.0 : keep;
    }

    public static boolean beyond(BlockPos pos, double keep, int viewDistance) {
        double dx = pos.getX() + 0.5 - camX;
        double dy = pos.getY() + 0.5 - camY;
        double dz = pos.getZ() + 0.5 - camZ;
        double allowed = viewDistance * keep;
        return dx * dx + dy * dy + dz * dz > allowed * allowed;
    }

    private static double keep(double dx, double dz) {
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 1.0E-3) {
            return 1.0;
        }
        double cos = (dx * fwdX + dz * fwdZ) / horizontal;
        if (cos >= centerCos) {
            return 1.0;
        }
        double angle = Math.acos(Math.max(-1.0, cos));
        double w = Math.min(1.0, (angle - centerAngle) * invRamp);
        return 1.0 - (1.0 - EDGE_KEEP) * w * w;
    }
}
