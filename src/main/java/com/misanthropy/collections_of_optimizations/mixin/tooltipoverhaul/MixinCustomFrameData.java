package com.misanthropy.collections_of_optimizations.mixin.tooltipoverhaul;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(targets = "dev.xylonity.tooltipoverhaul.client.frame.CustomFrameData", remap = false)
public abstract class MixinCustomFrameData {

    @Unique
    private volatile List<?> coo$tagKeys;

    @WrapMethod(method = "getTagKeys", require = 0)
    private List<?> coo$cacheTagKeys(Operation<List<?>> original) {
        if (!CoOConfig.tooltipoverhaulCacheFrameTagKeys) {
            return original.call();
        }
        List<?> cached = this.coo$tagKeys;
        if (cached == null) {
            cached = original.call();
            this.coo$tagKeys = cached;
        }
        return cached;
    }
}
