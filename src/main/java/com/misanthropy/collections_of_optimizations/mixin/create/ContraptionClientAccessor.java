package com.misanthropy.collections_of_optimizations.mixin.create;

import com.simibubi.create.content.contraptions.Contraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = Contraption.class, remap = false)
public interface ContraptionClientAccessor {

    @Accessor("clientContraption")
    AtomicReference<?> coo$clientContraption();
}
