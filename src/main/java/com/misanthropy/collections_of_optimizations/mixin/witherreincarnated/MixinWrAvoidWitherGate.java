package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.misanthropy.collections_of_optimizations.core.WrState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.alexander.witherreincarnated.events.ModifyAIEvent$5", remap = false)
public abstract class MixinWrAvoidWitherGate {

    @Inject(method = {"m_8036_", "canUse"}, at = @At("HEAD"), cancellable = true, require = 1)
    private void coo$noWitherNoScan(CallbackInfoReturnable<Boolean> cir) {
        if (WrState.noServerWithers()) {
            cir.setReturnValue(false);
        }
    }
}
