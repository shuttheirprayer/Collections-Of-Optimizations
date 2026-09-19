package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PoseStack.class, priority = 1500)
public abstract class MixinPoseStackTrackingGate {

    @Unique
    private static final String COO_FANCYMENU_POSE_STACK = "de.keksuccino.fancymenu.mixin.mixins.common.client.MixinPoseStack";

    @Unique
    private static boolean coo$tracking() {
        return !CoOConfig.fancymenuTrackPoseStackOnlyInScreens || Minecraft.getInstance().screen != null;
    }

    @TargetHandler(mixin = COO_FANCYMENU_POSE_STACK, name = "after_pushPose_FancyMenu")
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$gatePush(CallbackInfo info, Operation<Void> original) {
        if (coo$tracking()) {
            original.call(info);
        }
    }

    @TargetHandler(mixin = COO_FANCYMENU_POSE_STACK, name = "after_popPose_FancyMenu")
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$gatePop(CallbackInfo info, Operation<Void> original) {
        if (coo$tracking()) {
            original.call(info);
        }
    }

    @TargetHandler(mixin = COO_FANCYMENU_POSE_STACK, name = "after_scale_FancyMenu")
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$gateScale(float x, float y, float z, CallbackInfo info, Operation<Void> original) {
        if (coo$tracking()) {
            original.call(x, y, z, info);
        }
    }

    @TargetHandler(mixin = COO_FANCYMENU_POSE_STACK, name = "after_mulPose_FancyMenu")
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$gateMulPose(Quaternionf quaternion, CallbackInfo info, Operation<Void> original) {
        if (coo$tracking()) {
            original.call(quaternion, info);
        }
    }

    @TargetHandler(mixin = COO_FANCYMENU_POSE_STACK, name = "after_translate_FancyMenu")
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$gateTranslate(float x, float y, float z, CallbackInfo info, Operation<Void> original) {
        if (coo$tracking()) {
            original.call(x, y, z, info);
        }
    }
}
