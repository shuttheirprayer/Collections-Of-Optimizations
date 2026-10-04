package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidModel.class, priority = 1500)
public abstract class MixinCataclysmArmPoseEvent {

    @TargetHandler(
            mixin = "com.github.L_Ender.cataclysm.mixin.Client.HumanoidModelMixin",
            name = "custom_poseRightArm"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$rightArmOnlyWhenUsing(LivingEntity entity, CallbackInfo ci, Operation<Void> original) {
        if (coo$mayPose(entity)) {
            original.call(entity, ci);
        }
    }

    @TargetHandler(
            mixin = "com.github.L_Ender.cataclysm.mixin.Client.HumanoidModelMixin",
            name = "custom_poseLeftArm"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$leftArmOnlyWhenUsing(LivingEntity entity, CallbackInfo ci, Operation<Void> original) {
        if (coo$mayPose(entity)) {
            original.call(entity, ci);
        }
    }

    @Unique
    private static boolean coo$mayPose(LivingEntity entity) {
        return !CoOConfig.cataclysmSkipIdleArmPoseEvent
                || entity == null
                || entity.isUsingItem() && !entity.getOffhandItem().isEmpty();
    }
}
