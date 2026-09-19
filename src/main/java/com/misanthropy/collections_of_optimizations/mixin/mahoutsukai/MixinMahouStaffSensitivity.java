package com.misanthropy.collections_of_optimizations.mixin.mahoutsukai;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"stepsword.mahoutsukai.handlers.ServerHandler"}, remap = false)
public abstract class MixinMahouStaffSensitivity {

    @Inject(method = "tickCheck", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$onlyLocalPlayerOnClient(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.mahoutsukaiFixStaffSensitivity || event == null || event.player == null) {
            return;
        }
        if (!event.player.level().isClientSide) {
            return;
        }
        if (event.player != Minecraft.getInstance().player) {
            ci.cancel();
        }
    }
}
