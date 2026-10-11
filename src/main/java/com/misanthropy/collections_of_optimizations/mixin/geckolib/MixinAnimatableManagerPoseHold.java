package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.misanthropy.collections_of_optimizations.core.GeckoPoseHold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.core.animation.AnimatableManager;

@Mixin(value = AnimatableManager.class, remap = false)
public abstract class MixinAnimatableManagerPoseHold implements GeckoPoseHold.Holder {

    @Unique
    private GeckoPoseHold coo$poseHold;

    @Override
    public GeckoPoseHold coo$poseHold() {
        GeckoPoseHold hold = coo$poseHold;
        if (hold == null) {
            hold = coo$poseHold = new GeckoPoseHold();
        }
        return hold;
    }
}
