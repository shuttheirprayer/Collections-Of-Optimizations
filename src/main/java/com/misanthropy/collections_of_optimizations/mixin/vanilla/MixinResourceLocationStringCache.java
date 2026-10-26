package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourceLocation.class)
public abstract class MixinResourceLocationStringCache {

    @Unique
    private String coo$string;

    @WrapMethod(method = "toString")
    private String coo$cachedString(Operation<String> original) {
        String cached = this.coo$string;
        if (cached != null) {
            return cached;
        }
        String built = original.call();
        if (CoOConfig.vanillaCacheResourceLocationString && ((Object) this).getClass() == ResourceLocation.class) {
            this.coo$string = built;
        }
        return built;
    }
}
