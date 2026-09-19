package com.misanthropy.collections_of_optimizations.mixin.tomeofblood;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.mystchonky.tomeofblood.TomeOfBlood", remap = false)
public abstract class MixinTobServerStartLog {

    @Inject(
            method = "onServerStarting(Lnet/minecraftforge/event/server/ServerStartingEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipStartupGreeting(ServerStartingEvent event, CallbackInfo ci) {
        if (!CoOConfig.tomeofbloodSilenceServerStartLog) {
            return;
        }
        ci.cancel();
    }
}
