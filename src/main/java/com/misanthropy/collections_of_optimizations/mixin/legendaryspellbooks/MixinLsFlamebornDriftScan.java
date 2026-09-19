package com.misanthropy.collections_of_optimizations.mixin.legendaryspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.higurashi.legendary_spellbooks.common.effects.handler.FlamebornDriftSpellHandler", remap = false)
public abstract class MixinLsFlamebornDriftScan {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$localPlayerOnly(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.legendaryspellbooksLocalPlayerDriftScan || event == null) {
            return;
        }
        if (event.player != Minecraft.getInstance().player) {
            ci.cancel();
        }
    }
}
