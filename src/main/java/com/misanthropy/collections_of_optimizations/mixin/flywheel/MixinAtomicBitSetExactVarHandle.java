package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import dev.engine_room.flywheel.backend.util.AtomicBitSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AtomicBitSet.class, remap = false)
public abstract class MixinAtomicBitSetExactVarHandle {
}
