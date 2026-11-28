package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TrackedBroadcast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AlexsCaves.class, remap = false)
public abstract class MixinAlexsCavesTrackedBroadcast {

    @Inject(method = "sendMSGToAll", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$trackingOnly(Object message, CallbackInfo ci) {
        if (!CoOConfig.alexscavesTrackingOnlyBroadcast) {
            return;
        }
        int id;
        if (message instanceof UpdateEffectVisualityEntityMessage effect) {
            id = ((ACUpdateEffectVisualityMessageAccess) effect).coo$entityId();
        } else if (message instanceof UpdateItemTagMessage tag) {
            id = ((ACUpdateItemTagMessageAccess) tag).coo$entityId();
        } else {
            return;
        }
        if (TrackedBroadcast.send(AlexsCaves.NETWORK_WRAPPER, message, id)) {
            ci.cancel();
        }
    }
}
