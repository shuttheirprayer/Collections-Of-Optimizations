package com.misanthropy.collections_of_optimizations.mixin.brutalbosses;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.brutalbosses.event.ClientEventHandler", remap = false)
public abstract class MixinBrutalBossesClientEvents {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanBossUiTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.brutalbossesLeanBossUiTick) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            ci.cancel();
            return;
        }
        LocalPlayer local = Minecraft.getInstance().player;
        if (local == null || event.player != local) {
            ci.cancel();
        }
    }
}
