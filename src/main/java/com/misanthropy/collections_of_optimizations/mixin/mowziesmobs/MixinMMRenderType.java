package com.misanthropy.collections_of_optimizations.mixin.mowziesmobs;

import com.bobmowzie.mowziesmobs.client.render.MMRenderType;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = MMRenderType.class, remap = false)
public abstract class MixinMMRenderType {

    @Unique
    private static final Map<ResourceLocation, RenderType> COO$GLOWING_EFFECT = new HashMap<>();

    @Unique
    private static final Map<ResourceLocation, RenderType> COO$SOLAR_FLARE = new HashMap<>();

    @WrapMethod(method = "getGlowingEffect", require = 0)
    private static RenderType coo$memoGlowingEffect(ResourceLocation location, Operation<RenderType> original) {
        return coo$memoRenderType(COO$GLOWING_EFFECT, location, original);
    }

    @WrapMethod(method = "getSolarFlare", require = 0)
    private static RenderType coo$memoSolarFlare(ResourceLocation location, Operation<RenderType> original) {
        return coo$memoRenderType(COO$SOLAR_FLARE, location, original);
    }

    @Unique
    private static RenderType coo$memoRenderType(Map<ResourceLocation, RenderType> cache, ResourceLocation location,
                                                 Operation<RenderType> original) {
        if (!CoOConfig.mowziesmobsCacheEffectRenderTypes) {
            return original.call(location);
        }
        RenderType cached = cache.get(location);
        if (cached != null) {
            return cached;
        }
        RenderType resolved = original.call(location);
        if (resolved != null) {
            cache.put(location, resolved);
        }
        return resolved;
    }
}
