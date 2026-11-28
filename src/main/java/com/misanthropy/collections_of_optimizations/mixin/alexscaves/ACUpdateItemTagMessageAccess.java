package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = UpdateItemTagMessage.class, remap = false)
public interface ACUpdateItemTagMessageAccess {

    @Accessor("entityId")
    int coo$entityId();
}
