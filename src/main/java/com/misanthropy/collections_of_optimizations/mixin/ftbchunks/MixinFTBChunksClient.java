package com.misanthropy.collections_of_optimizations.mixin.ftbchunks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtbRegionImageGen;
import com.mojang.blaze3d.platform.NativeImage;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import dev.ftb.mods.ftblibrary.math.XZ;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    private int minimapTextureId;

    @Unique
    private MapRegion coo$uploadRegion;

    @Unique
    private NativeImage coo$uploadImage;

    @Unique
    private int coo$stampTexture = -1;

    @Unique
    private NativeImage[] coo$tileImage;

    @Unique
    private int[] coo$tileGen;

    @Unique
    private int[] coo$tileSrc;

    @Inject(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$dropMinimapMemos(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        coo$lastImageRegion = null;
        coo$lastRegionImage = null;
        coo$uploadRegion = null;
        coo$uploadImage = null;
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
        NativeImage image;
        if (!CoOConfig.ftbchunksMemoMinimapRegions) {
            image = original.call(region);
        } else if (coo$lastRegionImage != null && coo$lastImageRegion == region) {
            image = coo$lastRegionImage;
        } else {
            image = original.call(region);
            coo$lastImageRegion = region;
            coo$lastRegionImage = image;
        }
        coo$uploadRegion = region;
        coo$uploadImage = image;
        return image;
    }

    @WrapOperation(
            method = "renderHud(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/NativeImage;upload(IIIIIIIZZZZ)V",
                    remap = true
            ),
            require = 0
    )
    private void coo$coalesceTileUpload(NativeImage image, int level, int x, int y, int srcX, int srcY,
                                        int width, int height, boolean blur, boolean clamp, boolean mipmap,
                                        boolean autoClose, Operation<Void> original) {
        MapRegion region = coo$uploadRegion;
        if (!CoOConfig.ftbchunksCoalesceMinimapUploads
                || image != coo$uploadImage
                || !(region instanceof FtbRegionImageGen stamped)
                || level != 0 || width != 16 || height != 16 || mipmap || autoClose
                || ((x | y | srcX | srcY) & 15) != 0
                || x < 0 || y < 0 || x >= 240 || y >= 240
                || srcX < 0 || srcY < 0 || srcX + 16 > image.getWidth() || srcY + 16 > image.getHeight()) {
            coo$stampTexture = -1;
            original.call(image, level, x, y, srcX, srcY, width, height, blur, clamp, mipmap, autoClose);
            return;
        }

        if (coo$tileImage == null || coo$stampTexture != minimapTextureId) {
            if (coo$tileImage == null) {
                coo$tileImage = new NativeImage[225];
                coo$tileGen = new int[225];
                coo$tileSrc = new int[225];
            } else {
                Arrays.fill(coo$tileImage, null);
            }
            coo$stampTexture = minimapTextureId;
        }

        int tile = (y >> 4) * 15 + (x >> 4);
        int gen = stamped.coo$imageGen();
        if ((gen & 1) != 0) {
            coo$tileImage[tile] = null;
            original.call(image, level, x, y, srcX, srcY, width, height, blur, clamp, mipmap, autoClose);
            return;
        }
        if (coo$tileImage[tile] == image && coo$tileGen[tile] == gen && coo$tileSrc[tile] == (srcX | srcY << 16)) {
            return;
        }

        int cols = Math.min(240 - x, image.getWidth() - srcX) >> 4;
        int rows = Math.min(240 - y, image.getHeight() - srcY) >> 4;
        coo$stampTexture = -1;
        original.call(image, 0, x, y, srcX, srcY, cols << 4, rows << 4, blur, clamp, false, false);
        coo$stampTexture = minimapTextureId;

        for (int row = 0; row < rows; row++) {
            int base = tile + row * 15;
            int src = srcX | (srcY + (row << 4)) << 16;
            for (int col = 0; col < cols; col++) {
                coo$tileImage[base + col] = image;
                coo$tileGen[base + col] = gen;
                coo$tileSrc[base + col] = src + (col << 4);
            }
        }
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
