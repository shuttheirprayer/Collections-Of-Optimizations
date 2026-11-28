package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.server.message.AnimationMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AnimationMessage.class, remap = false)
public interface CitadelAnimationMessageAccess {

    @Accessor("entityID")
    int coo$entityId();
}
