package com.misanthropy.collections_of_optimizations.mixin.ftbxmodcompat;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.ftb.mods.ftbchunks.api.client.event.MapIconEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbxmodcompat.ftbchunks.waystones.WaystonesCommon", remap = false)
public abstract class MixinWaystonesMapIcons {

    @Inject(
            method = "mapWidgets",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipWaystoneIcons(MapIconEvent event, CallbackInfo ci) {
        if (CoOConfig.ftbchunksDisableWaystoneIcons) {
            ci.cancel();
        }
    }
}
