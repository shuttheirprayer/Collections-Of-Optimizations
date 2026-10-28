package com.misanthropy.collections_of_optimizations.mixin.biggerstacks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import portb.biggerstacks.util.CallingClassUtil;

@Mixin(value = CallingClassUtil.class, remap = false)
public abstract class MixinCallingClassUtil {

    @Inject(method = "getCallerClassName", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipStackWalk(CallbackInfoReturnable<String> cir) {
        if (CoOConfig.biggerstacksSkipCallerLookup) {
            cir.setReturnValue("?");
        }
    }
}
