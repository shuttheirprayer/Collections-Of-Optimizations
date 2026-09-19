package com.misanthropy.collections_of_optimizations.mixin.ftbchunks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.platform.NativeImage;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import dev.ftb.mods.ftblibrary.math.XZ;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FTBChunksClient.class, remap = false)
public abstract class MixinFTBChunksClient {

    @Unique
    private Map<XZ, MapRegion> coo$regionMemo;

    @Unique
    private MapDimension coo$memoDimension;

    @Unique
    private MapRegion coo$lastImageRegion;

    @Unique
    private NativeImage coo$lastRegionImage;

    @Inject(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$dropMinimapMemos(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        coo$lastImageRegion = null;
        coo$lastRegionImage = null;
        if (coo$regionMemo != null) {
            coo$regionMemo.clear();
        }
    }

    @WrapOperation(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ftb/mods/ftbchunks/client/map/MapDimension;getRegion(Ldev/ftb/mods/ftblibrary/math/XZ;)Ldev/ftb/mods/ftbchunks/client/map/MapRegion;"
            ),
            require = 0
    )
    private MapRegion coo$memoRegion(MapDimension dimension, XZ pos, Operation<MapRegion> original) {
        if (!CoOConfig.ftbchunksMemoMinimapRegions) {
            return original.call(dimension, pos);
        }

        if (coo$regionMemo == null) {
            coo$regionMemo = new HashMap<>();
        }
        if (coo$memoDimension != dimension) {
            coo$regionMemo.clear();
            coo$memoDimension = dimension;
        }

        MapRegion memoized = coo$regionMemo.get(pos);
        if (memoized != null) {
            return memoized;
        }

        MapRegion region = original.call(dimension, pos);
        if (region != null) {
            coo$regionMemo.put(pos, region);
        }
        return region;
    }

    @WrapOperation(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ftb/mods/ftbchunks/client/map/MapRegion;getRenderedMapImage()Lcom/mojang/blaze3d/platform/NativeImage;"
            ),
            require = 0
    )
    private NativeImage coo$memoRegionImage(MapRegion region, Operation<NativeImage> original) {
        if (!CoOConfig.ftbchunksMemoMinimapRegions) {
            return original.call(region);
        }

        if (coo$lastRegionImage != null && coo$lastImageRegion == region) {
            return coo$lastRegionImage;
        }

        NativeImage image = original.call(region);
        coo$lastImageRegion = region;
        coo$lastRegionImage = image;
        return image;
    }

    @Inject(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ftb/mods/ftbchunks/client/FTBChunksClient;getZoom()F"
            ),
            cancellable = true,
            require = 0
    )
    private void coo$skipHiddenMinimapWork(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        if (!CoOConfig.ftbchunksSkipHiddenMinimapWork) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.screen != null) {
            return;
        }

        if (mc.options.renderDebug
                || !FTBChunksClientConfig.MINIMAP_ENABLED.get()
                || FTBChunksClientConfig.MINIMAP_VISIBILITY.get() == 0
                || !FTBChunksWorldConfig.shouldShowMinimap(mc.player)) {
            ci.cancel();
        }
    }
}
