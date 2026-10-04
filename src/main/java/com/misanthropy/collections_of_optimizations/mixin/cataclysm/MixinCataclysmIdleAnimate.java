package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.model.entity.Amethyst_Crab_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ancient_Remnant_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ender_Guardian_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ignis_Model;
import com.github.L_Ender.cataclysm.client.model.entity.The_Leviathan_Model;
import com.github.L_Ender.lionfishapi.client.model.Animations.ModelAnimator;
import com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Ignis_Model.class, The_Leviathan_Model.class, Ancient_Remnant_Model.class, Ender_Guardian_Model.class, Amethyst_Crab_Model.class}, remap = false)
public abstract class MixinCataclysmIdleAnimate {

    @Shadow(remap = false)
    private ModelAnimator animator;

    @Inject(
            method = "animate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/L_Ender/lionfishapi/client/model/Animations/ModelAnimator;update(Lcom/github/L_Ender/lionfishapi/server/animation/IAnimatedEntity;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true,
            require = 0
    )
    private void coo$skipIdleKeyframes(CallbackInfo ci) {
        if (CoOConfig.cataclysmSkipIdleAnimate) {
            IAnimatedEntity entity = this.animator.getEntity();
            if (entity != null && entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                ci.cancel();
            }
        }
    }
}
