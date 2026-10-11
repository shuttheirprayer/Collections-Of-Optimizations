package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.GeckoPoseHold;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.Collection;

@Mixin(value = AnimationProcessor.class, remap = false)
public abstract class MixinAnimationProcessorPoseHold {

    @Shadow
    public boolean reloadAnimations;

    @Shadow
    public abstract Collection<CoreGeoBone> getRegisteredBones();

    @Unique
    private int coo$boneEpoch;

    @Inject(method = "setActiveModel", at = @At("RETURN"), require = 0)
    private void coo$bumpBoneEpoch(CallbackInfo ci) {
        coo$boneEpoch++;
    }

    @SuppressWarnings("rawtypes")
    @WrapMethod(
            method = "tickAnimation(Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/core/animatable/model/CoreGeoModel;Lsoftware/bernie/geckolib/core/animation/AnimatableManager;DLsoftware/bernie/geckolib/core/animation/AnimationState;Z)V",
            require = 0
    )
    private void coo$holdDistantPose(GeoAnimatable animatable, CoreGeoModel model, AnimatableManager manager,
                                     double animTime, AnimationState state, boolean crashWhenCantFindBone,
                                     Operation<Void> original) {
        if (CoOConfig.geckolibHoldDistantPosePixels <= 0
                || !(animatable instanceof Entity entity)
                || reloadAnimations
                || manager.isFirstTick()
                || !(manager instanceof GeckoPoseHold.Holder holder)
                || !GeckoPoseHold.eligible(entity)) {
            original.call(animatable, model, manager, animTime, state, crashWhenCantFindBone);
            return;
        }
        GeckoPoseHold hold = holder.coo$poseHold();
        Collection<CoreGeoBone> bones = getRegisteredBones();
        double bucket = Math.floor(animTime);
        if (hold.tryHold(this, coo$boneEpoch, bones, bucket, entity, manager, state)) {
            return;
        }
        original.call(animatable, model, manager, animTime, state, crashWhenCantFindBone);
        hold.capture(this, coo$boneEpoch, bones, bucket);
    }
}
