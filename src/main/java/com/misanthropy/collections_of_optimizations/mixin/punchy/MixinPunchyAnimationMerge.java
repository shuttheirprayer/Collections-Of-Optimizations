package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PunchyMemos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchy.client.render.ToolTuning;
import punchy.config.PunchyTuningConfig;

import java.util.List;

@Mixin(value = ToolTuning.class, remap = false)
public abstract class MixinPunchyAnimationMerge {

    @SuppressWarnings("unchecked")
    @WrapMethod(method = "mergeAnimationEntries", require = 0)
    private static List<PunchyTuningConfig.CustomAnimationEntry> coo$memoMerge(
            List<PunchyTuningConfig.CustomAnimationEntry> primary,
            List<PunchyTuningConfig.CustomAnimationEntry> defaults,
            Operation<List<PunchyTuningConfig.CustomAnimationEntry>> original) {
        if (!CoOConfig.punchyMemoAnimationMerge) {
            return original.call(primary, defaults);
        }
        Object known = PunchyMemos.merged(primary, defaults);
        if (known != null) {
            return (List<PunchyTuningConfig.CustomAnimationEntry>) PunchyMemos.unwrapMerged(known);
        }
        List<PunchyTuningConfig.CustomAnimationEntry> merged = original.call(primary, defaults);
        PunchyMemos.rememberMerged(primary, defaults, merged);
        return merged;
    }

    @Inject(method = "clearCustomItemAnimationCache", at = @At("HEAD"), require = 0)
    private static void coo$forgetMerged(CallbackInfo ci) {
        PunchyMemos.forgetMerged();
    }
}
