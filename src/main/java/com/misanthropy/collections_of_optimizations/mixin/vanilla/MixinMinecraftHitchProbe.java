package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.HitchProbe;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftHitchProbe {

    @Inject(method = "runTick", at = @At("HEAD"))
    private void coo$hitchStart(boolean renderLevel, CallbackInfo ci) {
        if (HitchProbe.ENABLED) {
            HitchProbe.tickStart();
        }
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void coo$hitchEnd(boolean renderLevel, CallbackInfo ci) {
        if (HitchProbe.ENABLED) {
            HitchProbe.tickEnd();
        }
    }
}
