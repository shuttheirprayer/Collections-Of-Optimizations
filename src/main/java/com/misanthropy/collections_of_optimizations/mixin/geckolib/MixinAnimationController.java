package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.keyframe.BoneAnimationQueue;

import java.util.Collection;
import java.util.Map;

@Mixin(value = AnimationController.class, remap = false)
public abstract class MixinAnimationController {

    @Shadow
    @Final
    protected Map<String, BoneAnimationQueue> boneAnimationQueues;

    @WrapMethod(method = "createInitialQueues", require = 0)
    private void coo$reuseQueues(Collection<CoreGeoBone> bones, Operation<Void> original) {
        Map<String, BoneAnimationQueue> queues = this.boneAnimationQueues;
        if (!CoOConfig.geckolibReuseBoneQueues || queues.size() != bones.size()) {
            original.call(bones);
            return;
        }
        for (CoreGeoBone bone : bones) {
            BoneAnimationQueue queue = queues.get(bone.getName());
            if (queue == null || queue.bone() != bone) {
                original.call(bones);
                return;
            }
        }
        for (BoneAnimationQueue queue : queues.values()) {
            queue.rotationXQueue().clear();
            queue.rotationYQueue().clear();
            queue.rotationZQueue().clear();
            queue.positionXQueue().clear();
            queue.positionYQueue().clear();
            queue.positionZQueue().clear();
            queue.scaleXQueue().clear();
            queue.scaleYQueue().clear();
            queue.scaleZQueue().clear();
        }
    }
}
