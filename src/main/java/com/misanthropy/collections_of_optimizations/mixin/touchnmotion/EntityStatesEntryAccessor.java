package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import strm.touchnmotion.interaction.EntityStates;

@Mixin(value = EntityStates.Entry.class, remap = false)
public interface EntityStatesEntryAccessor {

    @Invoker("<init>")
    static EntityStates.Entry<?> coo$create(Object value) {
        throw new AssertionError();
    }

    @Accessor("seenAt")
    long coo$seenAt();

    @Accessor("seenAt")
    void coo$setSeenAt(long seenAt);
}
