package com.misanthropy.collections_of_optimizations.mixin.modularrouters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "me.desht.modularrouters.block.tile.ModularRouterBlockEntity", remap = false)
public abstract class MixinRouterBeamTick {

    @Shadow
    @Final
    public List<?> beams;

    @Inject(
            method = "clientTick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipEmptyBeamTick(CallbackInfo ci) {
        if (!CoOConfig.modularroutersSkipEmptyBeamTick) {
            return;
        }

        List<?> current = this.beams;
        if (current != null && current.isEmpty()) {
            ci.cancel();
        }
    }
}
