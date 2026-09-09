package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.lwjgl.opengl.GL15;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.graphics.PixelBuffers;
import xaero.map.region.texture.RegionTexture;

import java.nio.ByteBuffer;

@Mixin(value = RegionTexture.class, remap = false)
public abstract class MixinRegionTextureUnpackPbo {

    @Redirect(
            method = "writeToUnpackPBO(ILxaero/map/pool/buffer/PoolTextureDirectBufferUnit;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/graphics/PixelBuffers;glMapBuffer(II)Ljava/nio/ByteBuffer;"
            ),
            require = 0
    )
    private ByteBuffer coo$orphanBeforeMap(int target, int access) {
        if (CoOConfig.xaeroworldmapOrphanUploadPbo) {
            PixelBuffers.glBufferData(target, RegionTexture.PBO_UNPACK_LENGTH, GL15.GL_STREAM_DRAW);
        }
        return PixelBuffers.glMapBuffer(target, access);
    }
}
