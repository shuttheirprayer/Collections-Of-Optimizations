package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;

@Mixin(targets = "punchy.client.state.SpearStateMachine", remap = false)
public abstract class MixinPunchySpearChargeType {

    @Unique
    private static final IdentityHashMap<String, Boolean> coo$known = new IdentityHashMap<>();

    @Inject(method = "isSpearChargeType", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$knownType(punchy.config.PunchyTuningConfig.CustomAnimationEntry entry, CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.punchyCacheBlacklistMatch && entry != null && entry.type != null) {
            Boolean known = coo$known.get(entry.type);
            if (known != null) {
                cir.setReturnValue(known);
            }
        }
    }

    @Inject(method = "isSpearChargeType", at = @At("RETURN"), require = 0)
    private static void coo$rememberType(punchy.config.PunchyTuningConfig.CustomAnimationEntry entry, CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.punchyCacheBlacklistMatch && entry != null && entry.type != null && coo$known.size() < 256) {
            coo$known.put(entry.type, cir.getReturnValue());
        }
    }
}
