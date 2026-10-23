package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.core.XaeroEmptyHeights;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.misc.ConsistentBitArray;

@Mixin(value = ConsistentBitArray.class, remap = false)
public abstract class MixinConsistentBitArrayCopyOnWrite {

    @Shadow
    private long[] data;

    @Redirect(method = "set", at = @At(value = "FIELD", target = "Lxaero/map/misc/ConsistentBitArray;data:[J", opcode = Opcodes.GETFIELD), require = 0)
    private long[] coo$writableData(ConsistentBitArray self) {
        long[] current = this.data;
        if (current == XaeroEmptyHeights.SHARED) {
            current = new long[current.length];
            this.data = current;
        }
        return current;
    }
}
