package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FrameDeferred;
import einstein.subtle_effects.SubtleEffectsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SubtleEffectsClient.class, remap = false)
public abstract class MixinSubtleEffectsDeferredTick {

    @WrapMethod(method = "clientTick")
    private static void coo$deferTickers(Minecraft minecraft, Level level, Operation<Void> original) {
        if (!CoOConfig.subtleeffectsDeferTickers || !FrameDeferred.ticking()) {
            original.call(minecraft, level);
            return;
        }
        FrameDeferred.defer(minecraft.level, () -> original.call(minecraft, level));
    }
}
