package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.graphics.PixelBuffers;
import xaero.map.region.texture.RegionTexture;

import java.nio.ByteBuffer;

@Mixin(value = RegionTexture.class, remap = false)
public abstract class MixinRegionTextureUnpackPbo {

    @Unique
    private static final int coo$STREAM_MAP = GL30.GL_MAP_WRITE_BIT | GL30.GL_MAP_INVALIDATE_BUFFER_BIT | GL30.GL_MAP_UNSYNCHRONIZED_BIT;

    @Unique
    private static byte coo$mapRange;

    @Redirect(
            method = "writeToUnpackPBO(ILxaero/map/pool/buffer/PoolTextureDirectBufferUnit;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/graphics/PixelBuffers;glMapBuffer(II)Ljava/nio/ByteBuffer;"
            ),
            require = 0
    )
    private ByteBuffer coo$orphanBeforeMap(int target, int access) {
        if (!CoOConfig.xaeroworldmapOrphanUploadPbo) {
            return PixelBuffers.glMapBuffer(target, access);
        }
        PixelBuffers.glBufferData(target, RegionTexture.PBO_UNPACK_LENGTH, GL15.GL_STREAM_DRAW);
        if (access == GL15.GL_WRITE_ONLY && coo$canMapRange()) {
            return GL30.glMapBufferRange(target, 0, RegionTexture.PBO_UNPACK_LENGTH, coo$STREAM_MAP);
        }
        return PixelBuffers.glMapBuffer(target, access);
    }

    @Unique
    private static boolean coo$canMapRange() {
        byte state = coo$mapRange;
        if (state == 0) {
            state = GL.getCapabilities().OpenGL30 ? (byte) 1 : (byte) 2;
            coo$mapRange = state;
        }
        return state == 1;
    }
}
