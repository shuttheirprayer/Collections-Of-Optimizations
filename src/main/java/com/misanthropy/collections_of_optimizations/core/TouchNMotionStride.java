package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import strm.emfcompat.core.ik.IKFrame;
import strm.touchnmotion.interaction.Body;
import strm.touchnmotion.torso.Stride;

public final class TouchNMotionStride {

    private static final float[] ALONG = {0.0f, 0.5f, 1.0f};
    private static final float PIXELS = 16.0f;

    private TouchNMotionStride() {}

    public static boolean levelOrStock(AbstractClientPlayer player, IKFrame frame, ModelPart leg, Vector3f from, Vector3f to,
                                       double spread, double probe, double tolerance) {
        if (!CoOConfig.touchnmotionFastStrideProbe || !(probe > 1.0E-3)) return Stride.level(player, frame, leg, from, to, spread, probe, tolerance);
        boolean fast = level(player, frame, leg, from, to, spread, probe, tolerance);
        if (ProbeCheck.ON) ProbeCheck.compare("Stride.level", fast, Stride.level(player, frame, leg, from, to, spread, probe, tolerance));
        return fast;
    }

    public static boolean level(AbstractClientPlayer player, IKFrame frame, ModelPart leg, Vector3f from, Vector3f to,
                                double spread, double probe, double tolerance) {
        Vector3f sole = Body.tip(leg, 12.0f);
        Matrix4f m = frame.modelToWorld();
        Vec3 camera = frame.camera();
        Level level = player.level();
        LeanRay ray = LeanRay.get();
        ray.beginMemo(player.getUUID());
        CollisionContext context = ray.contextFor(player);
        try {
            return probe(ray, level, context, sole, m, camera, from, to, spread, probe, tolerance);
        } finally {
            ray.endMemo();
        }
    }

    private static boolean probe(LeanRay ray, Level level, CollisionContext context, Vector3f sole, Matrix4f m, Vec3 camera,
                                 Vector3f from, Vector3f to, double spread, double probe, double tolerance) {
        double height = Double.NaN;
        for (float t : ALONG) {
            float x = (sole.x + ((to.x - from.x) * t + from.x)) / PIXELS;
            float y = (sole.y + ((to.y - from.y) * t + from.y)) / PIXELS;
            float z = (sole.z + ((to.z - from.z) * t + from.z)) / PIXELS;
            double pointX = camera.x + (m.m00() * x + (m.m10() * y + (m.m20() * z + m.m30())));
            double pointY = camera.y + (m.m01() * x + (m.m11() * y + (m.m21() * z + m.m31())));
            double pointZ = camera.z + (m.m02() * x + (m.m12() * y + (m.m22() * z + m.m32())));
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    double px = pointX + (double) dx * spread + 0.0;
                    double py = pointY + 0.0;
                    double pz = pointZ + (double) dz * spread + 0.0;
                    if (!ray.cast(level, null, context, false, px, py + probe, pz, px, py + -probe, pz)) return false;
                    if (Math.abs(ray.hitY - pointY) > tolerance) return false;
                    if (Double.isNaN(height)) {
                        height = ray.hitY;
                    } else if (Math.abs(ray.hitY - height) > 0.04) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
