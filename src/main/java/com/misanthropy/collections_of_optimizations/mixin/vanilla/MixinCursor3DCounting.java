package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Cursor3D;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reimplemented from Lomka 0.5.4 by Starlev (LGPL-3.0). */
@Mixin(Cursor3D.class)
public abstract class MixinCursor3DCounting {

    @Shadow @Final private int width;
    @Shadow @Final private int height;
    @Shadow @Final private int end;
    @Shadow private int index;
    @Shadow private int x;
    @Shadow private int y;
    @Shadow private int z;

    @Inject(method = "advance", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$countingAdvance(CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.vanillaLeanBlockPosRange) {
            return;
        }
        if (this.index == this.end) {
            cir.setReturnValue(false);
            return;
        }
        // x, y, z are already 0 for the first position; every later one is a carry chain.
        if (this.index++ != 0 && ++this.x == this.width) {
            this.x = 0;
            if (++this.y == this.height) {
                this.y = 0;
                ++this.z;
            }
        }
        cir.setReturnValue(true);
    }
}
