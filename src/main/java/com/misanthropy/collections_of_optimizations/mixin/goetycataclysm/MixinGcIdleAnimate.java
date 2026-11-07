package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import com.github.L_Ender.lionfishapi.client.model.Animations.ModelAnimator;
import com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "com.Polarice3.goety_cataclysm.client.render.model.LeviathanServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.ZombieAmethystCrabModel"
}, remap = false)
public abstract class MixinGcIdleAnimate {
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
