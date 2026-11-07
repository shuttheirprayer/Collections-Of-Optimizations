package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.FrameStamp;
import com.misanthropy.collections_of_optimizations.core.SpreadBlockEntityTicks;
import com.misanthropy.collections_of_optimizations.core.SpreadClientTickListeners;
import com.misanthropy.collections_of_optimizations.core.SpreadEntityTicks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftSpreadEntityTicks {

    @Shadow
    public ClientLevel level;

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;turnPlayer()V"))
    private void coo$drainSpreadEntityTicks(boolean renderLevel, CallbackInfo ci) {
        FrameStamp.frame++;
        SpreadEntityTicks.drainFrame(this.level);
        SpreadBlockEntityTicks.drainFrame(this.level);
        SpreadClientTickListeners.drainFrame();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void coo$flushSpreadListenersOnTick(CallbackInfo ci) {
        SpreadClientTickListeners.flush();
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void coo$flushSpreadOnSetLevel(ClientLevel next, CallbackInfo ci) {
        SpreadClientTickListeners.flush();
        SpreadEntityTicks.flush();
        SpreadBlockEntityTicks.flush();
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void coo$flushSpreadOnClearLevel(Screen screen, CallbackInfo ci) {
        SpreadClientTickListeners.flush();
        SpreadEntityTicks.flush();
        SpreadBlockEntityTicks.flush();
    }
}
