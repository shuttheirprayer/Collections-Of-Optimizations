package com.misanthropy.collections_of_optimizations.mixin.moonlight;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MapItemSavedData.class, priority = 1500)
public abstract class MixinMapItemSavedData {

    @TargetHandler(
            mixin = "net.mehvahdjukaar.moonlight.core.mixins.MapDataMixin",
            name = "checkCustomDeco"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0,
            remap = false
    )
    private void coo$skipEmptyMapMarkerScan(BlockGetter world, int x, int z, CallbackInfo theirCallback, CallbackInfo ci) {
        if (CoOConfig.moonlightSkipEmptyMapMarkerScan
                && ((ExpandedMapData) this).getCustomMarkers().isEmpty()) {
            ci.cancel();
        }
    }
}
