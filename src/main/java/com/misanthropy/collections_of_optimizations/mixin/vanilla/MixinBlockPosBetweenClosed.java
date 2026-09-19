package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.AbstractIterator;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Reimplemented from Lomka 0.5.4 by Starlev (LGPL-3.0). */
@Mixin(BlockPos.class)
public abstract class MixinBlockPosBetweenClosed {

    @Inject(method = "betweenClosed(IIIIII)Ljava/lang/Iterable;", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$countingCursor(int x1, int y1, int z1, int x2, int y2, int z2, CallbackInfoReturnable<Iterable<BlockPos>> cir) {
        if (!CoOConfig.vanillaLeanBlockPosRange) {
            return;
        }
        if (x1 > x2 || y1 > y2 || z1 > z2) {
            cir.setReturnValue(List.of());
            return;
        }
        cir.setReturnValue(() -> new AbstractIterator<>() {
            private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            private int x = x1;
            private int y = y1;
            private int z = z1;

            @Override
            protected BlockPos computeNext() {
                if (this.z > z2) {
                    return this.endOfData();
                }
                BlockPos pos = this.cursor.set(this.x, this.y, this.z);
                if (++this.x > x2) {
                    this.x = x1;
                    if (++this.y > y2) {
                        this.y = y1;
                        ++this.z;
                    }
                }
                return pos;
            }
        });
    }
}
