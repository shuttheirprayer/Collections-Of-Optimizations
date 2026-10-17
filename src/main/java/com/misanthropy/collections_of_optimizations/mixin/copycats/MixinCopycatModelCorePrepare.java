package com.misanthropy.collections_of_optimizations.mixin.copycats;

import com.copycatsplus.copycats.config.CCConfigs;
import com.copycatsplus.copycats.config.CClient;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import java.lang.reflect.Method;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CopycatModelCore.class, remap = false)
public abstract class MixinCopycatModelCorePrepare {

    @Unique
    private static int coo$nextRefreshTick;

    @Unique
    private static boolean coo$cacheValid;

    @Unique
    private static boolean coo$enhanced = true;

    @Unique
    private static boolean coo$colorize;

    @Unique
    private static Object coo$enhancedValue;

    @Unique
    private static Object coo$colorizeValue;

    @Unique
    private static Method coo$getMethod;

    @Inject(method = "prepareForRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$cachedPrepare(CallbackInfo ci) {
        if (!CoOConfig.copycatsCachedModelConfig) {
            return;
        }
        int now = ClientTickStamp.current();
        if (!coo$cacheValid || now - coo$nextRefreshTick >= 0) {
            try {
                if (coo$getMethod == null) {
                    CClient client = CCConfigs.client();
                    coo$enhancedValue = CClient.class.getField("useEnhancedModels").get(client);
                    coo$colorizeValue = CClient.class.getField("colorizeMultiStates").get(client);
                    coo$getMethod = coo$enhancedValue.getClass().getMethod("get");
                }
                coo$enhanced = (Boolean) coo$getMethod.invoke(coo$enhancedValue);
                coo$colorize = (Boolean) coo$getMethod.invoke(coo$colorizeValue);
                coo$cacheValid = true;
                coo$nextRefreshTick = now + 20;
            } catch (Throwable t) {
                return;
            }
        }
        CopycatModelCore self = (CopycatModelCore) (Object) this;
        self.enhanced = coo$enhanced;
        self.colorize = coo$colorize;
        ci.cancel();
    }
}
