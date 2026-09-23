package com.misanthropy.collections_of_optimizations.mixin.modularrouters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.desht.modularrouters.logic.ModuleTarget", remap = false)
public abstract class MixinModuleTargetHandlerCache {

    @Shadow
    private LazyOptional<IItemHandler> cachedItemCap;

    @Inject(
            method = "getItemHandler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$reuseCachedItemHandler(CallbackInfoReturnable<LazyOptional<IItemHandler>> cir) {
        if (!CoOConfig.modularroutersCacheTargetHandlerLookup) {
            return;
        }

        LazyOptional<IItemHandler> cached = this.cachedItemCap;
        if (cached != null && cached.isPresent()) {
            cir.setReturnValue(cached);
        }
    }
}
