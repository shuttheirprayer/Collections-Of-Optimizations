package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.Collection;

public final class GeckoPoseHold {

    private Object processor;
    private int epoch;
    private double bucket = Double.NaN;
    private int boneCount = -1;
    private float[] pose = new float[0];

    public interface Holder {
        GeckoPoseHold coo$poseHold();
    }

    public static boolean eligible(Entity entity) {
        return !(entity instanceof Player)
                && entity != Minecraft.getInstance().getCameraEntity()
                && !entity.isVehicle()
                && !entity.isPassenger()
                && !entity.getType().is(Tags.EntityTypes.BOSSES);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean tryHold(Object processor, int epoch, Collection<CoreGeoBone> bones, double bucket,
                           Entity entity, AnimatableManager<?> manager, AnimationState state) {
        if (processor != this.processor || epoch != this.epoch || bucket != this.bucket
                || bones.size() != boneCount || !small(entity)) {
            return false;
        }
        Collection<? extends AnimationController<?>> controllers = manager.getAnimationControllers().values();
        for (AnimationController<?> controller : controllers) {
            if (controller.getAnimationState() != AnimationController.State.RUNNING
                    || controller.isPlayingTriggeredAnimation()) {
                return false;
            }
            AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
            if (current == null) {
                return false;
            }
            Animation animation = current.animation();
            Animation.Keyframes events = animation == null ? null : animation.keyFrames();
            if (events == null || events.sounds().length != 0 || events.particles().length != 0
                    || events.customInstructions().length != 0) {
                return false;
            }
        }
        for (AnimationController controller : controllers) {
            state.withController(controller);
        }
        float[] pose = this.pose;
        int i = 0;
        for (CoreGeoBone bone : bones) {
            bone.setRotX(pose[i]);
            bone.setRotY(pose[i + 1]);
            bone.setRotZ(pose[i + 2]);
            bone.setPosX(pose[i + 3]);
            bone.setPosY(pose[i + 4]);
            bone.setPosZ(pose[i + 5]);
            bone.setScaleX(pose[i + 6]);
            bone.setScaleY(pose[i + 7]);
            bone.setScaleZ(pose[i + 8]);
            bone.resetStateChanges();
            i += 9;
        }
        return true;
    }

    public void capture(Object processor, int epoch, Collection<CoreGeoBone> bones, double bucket) {
        int size = bones.size();
        float[] pose = this.pose;
        if (pose.length != size * 9) {
            pose = this.pose = new float[size * 9];
        }
        int i = 0;
        for (CoreGeoBone bone : bones) {
            if (i == pose.length) {
                this.boneCount = -1;
                return;
            }
            pose[i] = bone.getRotX();
            pose[i + 1] = bone.getRotY();
            pose[i + 2] = bone.getRotZ();
            pose[i + 3] = bone.getPosX();
            pose[i + 4] = bone.getPosY();
            pose[i + 5] = bone.getPosZ();
            pose[i + 6] = bone.getScaleX();
            pose[i + 7] = bone.getScaleY();
            pose[i + 8] = bone.getScaleZ();
            i += 9;
        }
        this.processor = processor;
        this.epoch = epoch;
        this.bucket = bucket;
        this.boneCount = i == pose.length ? size : -1;
    }

    private static boolean small(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        double dx = entity.getX() - camera.x;
        double dy = entity.getY() - camera.y;
        double dz = entity.getZ() - camera.z;
        AABB box = entity.getBoundingBoxForCulling();
        double extent = Math.max(box.getYsize(), Math.max(box.getXsize(), box.getZsize()));
        Matrix4f projection = RenderSystem.getProjectionMatrix();
        if (projection.m33() != 0.0F) {
            return false;
        }
        double pixels = extent * projection.m11() * mc.getWindow().getHeight() * 0.5;
        double limit = CoOConfig.geckolibHoldDistantPosePixels;
        return pixels > 0.0 && pixels * pixels < limit * limit * (dx * dx + dy * dy + dz * dz);
    }
}
