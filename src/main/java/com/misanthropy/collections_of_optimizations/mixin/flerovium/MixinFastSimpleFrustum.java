package com.misanthropy.collections_of_optimizations.mixin.flerovium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.moepus.flerovium.functions.Chunk.FastSimpleFrustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FastSimpleFrustum.class, remap = false)
public abstract class MixinFastSimpleFrustum {

    @Unique
    private static final float COO$BAKED_SIZE = 18.25F;

    @Shadow
    private float nxX;
    @Shadow
    private float nxY;
    @Shadow
    private float nxZ;
    @Shadow
    private float negNxW;
    @Shadow
    private float pxX;
    @Shadow
    private float pxY;
    @Shadow
    private float pxZ;
    @Shadow
    private float negPxW;
    @Shadow
    private float nyX;
    @Shadow
    private float nyY;
    @Shadow
    private float nyZ;
    @Shadow
    private float negNyW;
    @Shadow
    private float pyX;
    @Shadow
    private float pyY;
    @Shadow
    private float pyZ;
    @Shadow
    private float negPyW;
    @Shadow
    private float nzX;
    @Shadow
    private float nzY;
    @Shadow
    private float nzZ;
    @Shadow
    private float negNzW;

    @Inject(method = "testAab", at = @At("HEAD"), cancellable = true)
    private void coo$testRealSize(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.fleroviumRealBoxSize) {
            return;
        }
        float x = minX + maxX;
        float y = minY + maxY;
        float z = minZ + maxZ;
        float sx = maxX - minX - COO$BAKED_SIZE;
        float sy = maxY - minY - COO$BAKED_SIZE;
        float sz = maxZ - minZ - COO$BAKED_SIZE;
        cir.setReturnValue(coo$side(this.nxX, this.nxY, this.nxZ, x, y, z, sx, sy, sz) >= this.negNxW
                && coo$side(this.pxX, this.pxY, this.pxZ, x, y, z, sx, sy, sz) >= this.negPxW
                && coo$side(this.nyX, this.nyY, this.nyZ, x, y, z, sx, sy, sz) >= this.negNyW
                && coo$side(this.pyX, this.pyY, this.pyZ, x, y, z, sx, sy, sz) >= this.negPyW
                && coo$side(this.nzX, this.nzY, this.nzZ, x, y, z, sx, sy, sz) >= this.negNzW);
    }

    public boolean containsAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        if (!CoOConfig.fleroviumRealBoxSize) {
            return false;
        }
        float x = minX + maxX;
        float y = minY + maxY;
        float z = minZ + maxZ;
        float sx = minX - maxX - COO$BAKED_SIZE;
        float sy = minY - maxY - COO$BAKED_SIZE;
        float sz = minZ - maxZ - COO$BAKED_SIZE;
        return coo$side(this.nxX, this.nxY, this.nxZ, x, y, z, sx, sy, sz) >= this.negNxW
                && coo$side(this.pxX, this.pxY, this.pxZ, x, y, z, sx, sy, sz) >= this.negPxW
                && coo$side(this.nyX, this.nyY, this.nyZ, x, y, z, sx, sy, sz) >= this.negNyW
                && coo$side(this.pyX, this.pyY, this.pyZ, x, y, z, sx, sy, sz) >= this.negPyW
                && coo$side(this.nzX, this.nzY, this.nzZ, x, y, z, sx, sy, sz) >= this.negNzW;
    }

    @Unique
    private static float coo$side(float nx, float ny, float nz, float x, float y, float z, float sx, float sy, float sz) {
        return nx * x + ny * y + nz * z + Math.abs(nx) * sx + Math.abs(ny) * sy + Math.abs(nz) * sz;
    }
}
