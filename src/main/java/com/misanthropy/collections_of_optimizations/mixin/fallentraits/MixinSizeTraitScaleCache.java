package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import com.misanthropy.collections_of_optimizations.core.FtEntityState;
import com.misanthropy.collections_of_optimizations.core.FtSizeScaleCache;
import net.kayn.fallen_traits.client.SizeRenderContext;
import net.kayn.fallen_traits.content.traits.legendary.SizeTrait;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SizeTrait.class, remap = false)
public abstract class MixinSizeTraitScaleCache {

    @Inject(method = "getScale", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$reuseFrameScale(Entity entity, CallbackInfoReturnable<Float> cir) {
        if (FtSizeScaleCache.rendering && entity instanceof FtEntityState state && coo$cacheable(entity)
                && state.coo$ftScaleStamp() == ClientTickStamp.current()) {
            cir.setReturnValue(state.coo$ftScale());
        }
    }

    @ModifyReturnValue(method = "getScale", at = @At("RETURN"), require = 0)
    private static float coo$keepFrameScale(float scale, @Local(argsOnly = true) Entity entity) {
        if (FtSizeScaleCache.rendering && entity instanceof FtEntityState state && coo$cacheable(entity)) {
            state.coo$ftSetScale(scale, ClientTickStamp.current());
        }
        return scale;
    }

    @Unique
    private static boolean coo$cacheable(Entity entity) {
        return CoOConfig.fallentraitsCacheRenderScale && entity.level().isClientSide() && !SizeRenderContext.isInventoryPreview();
    }
}
