package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.message.AnimationMessage;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TrackedBroadcast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Citadel.class, remap = false)
public abstract class MixinCitadelTrackedBroadcast {

    @Inject(method = "sendMSGToAll", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$trackingOnly(Object message, CallbackInfo ci) {
        if (!CoOConfig.citadelTrackingOnlyBroadcast) {
            return;
        }
        int id;
        if (message instanceof AnimationMessage animation) {
            id = ((CitadelAnimationMessageAccess) animation).coo$entityId();
        } else if (message instanceof PropertiesMessage properties) {
            id = ((CitadelPropertiesMessageAccess) properties).coo$entityId();
        } else {
            return;
        }
        if (TrackedBroadcast.send(Citadel.NETWORK_WRAPPER, message, id)) {
            ci.cancel();
        }
    }
}
