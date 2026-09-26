package com.misanthropy.collections_of_optimizations.mixin.createaddition;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.mrh0.createaddition.event.ClientEventHandler", remap = false)
public abstract class MixinCAClientEventHandler {

    @Shadow(remap = false)
    public static boolean clientRenderHeldWire;

    @Inject(method = "tickSoundscapes", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$soundscapesOncePerTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (CoOConfig.createadditionSoundscapeTickOnce && event.phase == TickEvent.Phase.START) {
            ci.cancel();
        }
    }

    @Inject(method = "playerRendererEvent", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanHeldWireCheck(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.createadditionLeanHeldWireCheck) return;
        if (event.phase == TickEvent.Phase.START) {
            ci.cancel();
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getInventory().getSelected().isEmpty()) {
            clientRenderHeldWire = false;
            ci.cancel();
        }
    }
}
