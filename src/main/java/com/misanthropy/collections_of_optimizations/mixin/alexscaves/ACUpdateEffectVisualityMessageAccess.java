package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = UpdateEffectVisualityEntityMessage.class, remap = false)
public interface ACUpdateEffectVisualityMessageAccess {

    @Accessor("entityID")
    int coo$entityId();
}
