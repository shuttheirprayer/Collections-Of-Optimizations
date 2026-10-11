package com.misanthropy.collections_of_optimizations.mixin.radium;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.WeakHashMap;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.world.listeners.WorldBorderListenerOnceMulti", remap = false)
public abstract class MixinRadiumBorderListeners {

    @WrapWithCondition(
            method = {
                    "m_5904_(Lnet/minecraft/world/level/border/WorldBorder;I)V",
                    "m_5903_(Lnet/minecraft/world/level/border/WorldBorder;I)V",
                    "m_6315_(Lnet/minecraft/world/level/border/WorldBorder;D)V",
                    "m_6313_(Lnet/minecraft/world/level/border/WorldBorder;D)V"
            },
            at = @At(value = "INVOKE", target = "Ljava/util/WeakHashMap;clear()V"),
            require = 0
    )
    private boolean coo$keepSubscribersOnShapelessChange(WeakHashMap<?, ?> subscribers) {
        return !CoOConfig.radiumKeepBorderListeners;
    }
}
