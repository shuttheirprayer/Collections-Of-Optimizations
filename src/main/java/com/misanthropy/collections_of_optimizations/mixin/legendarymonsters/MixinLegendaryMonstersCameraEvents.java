package com.misanthropy.collections_of_optimizations.mixin.legendarymonsters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.client.event.ClientEvent", remap = false)
public abstract class MixinLegendaryMonstersCameraEvents {

    @Inject(method = "setDynamicZoom", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipDeadZoomWork(ViewportEvent.ComputeCameraAngles event, CallbackInfo ci) {
        if (CoOConfig.legendarymonstersSkipDeadZoomWork) {
            ci.cancel();
        }
    }

    @Inject(method = "onCameraSetup", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$guardCameraNullPlayer(ViewportEvent.ComputeCameraAngles event, CallbackInfo ci) {
        if (CoOConfig.legendarymonstersGuardCameraNullPlayer && Minecraft.getInstance().player == null) {
            ci.cancel();
        }
    }
}
