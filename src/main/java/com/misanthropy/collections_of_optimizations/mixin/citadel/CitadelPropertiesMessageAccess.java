package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PropertiesMessage.class, remap = false)
public interface CitadelPropertiesMessageAccess {

    @Accessor("entityID")
    int coo$entityId();
}
