package com.misanthropy.collections_of_optimizations.mixin.domesticationinnovation;

import com.github.alexthe668.domesticationinnovation.server.misc.CollarTickTracker;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mixin(value = CollarTickTracker.class, remap = false)
public abstract class MixinDICollarTickTracker {

    @Shadow
    @Final
    private Map<UUID, Integer> blockedCollarTagUpdates;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$safeCollarPrune(CallbackInfo ci) {
        if (!CoOConfig.domesticationinnovationSafeCollarPrune || this.blockedCollarTagUpdates == null) {
            return;
        }
        ci.cancel();
        if (this.blockedCollarTagUpdates.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Integer>> iterator = this.blockedCollarTagUpdates.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            Integer value = entry.getValue();
            int next = (value == null ? 0 : value) - 1;
            if (next < 0) {
                iterator.remove();
            } else {
                entry.setValue(next);
            }
        }
    }
}
