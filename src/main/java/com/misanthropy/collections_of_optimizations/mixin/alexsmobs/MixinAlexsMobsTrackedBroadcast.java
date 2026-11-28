package com.misanthropy.collections_of_optimizations.mixin.alexsmobs;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TrackedBroadcast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AlexsMobs.class, remap = false)
public abstract class MixinAlexsMobsTrackedBroadcast {

    @Inject(method = "sendMSGToAll", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$trackingOnly(Object message, CallbackInfo ci) {
        if (!CoOConfig.alexsmobsTrackingOnlyBroadcast) {
            return;
        }
        int id;
        if (message instanceof MessageHurtMultipart hurt) {
            id = hurt.parent;
        } else if (message instanceof MessageSendVisualFlagFromServer flag) {
            id = flag.entityID;
        } else {
            return;
        }
        if (TrackedBroadcast.send(AlexsMobs.NETWORK_WRAPPER, message, id)) {
            ci.cancel();
        }
    }
}
