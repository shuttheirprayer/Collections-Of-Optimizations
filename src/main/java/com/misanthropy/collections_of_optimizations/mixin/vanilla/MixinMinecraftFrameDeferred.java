package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.FrameDeferred;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftFrameDeferred {

    @Shadow
    public ClientLevel level;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void coo$runFrameDeferred(boolean renderLevel, CallbackInfo ci) {
        FrameDeferred.run(this.level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void coo$beginFrameDeferredTick(CallbackInfo ci) {
        FrameDeferred.begin(this.level);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void coo$endFrameDeferredTick(CallbackInfo ci) {
        FrameDeferred.end();
    }
}
