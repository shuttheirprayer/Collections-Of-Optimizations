package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Cursor3D;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cursor3D.class)
public abstract class MixinCursor3DCounting {

    @Shadow @Final private int width;
    @Shadow @Final private int height;
    @Shadow @Final private int depth;
    @Shadow @Final private int end;
    @Shadow private int index;
    @Shadow private int x;
    @Shadow private int y;
    @Shadow private int z;

    @Unique
    private boolean coo$countable;

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void coo$checkCountable(int originX, int originY, int originZ, int maxX, int maxY, int maxZ, CallbackInfo ci) {
        this.coo$countable = this.width > 0 && this.height > 0 && this.depth > 0
                && (long) this.width * this.height * this.depth == this.end;
    }

    @Inject(method = "advance", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$countingAdvance(CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.vanillaLeanBlockPosRange || !this.coo$countable) {
            return;
        }
        if (this.index == this.end) {
            cir.setReturnValue(false);
            return;
        }
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
