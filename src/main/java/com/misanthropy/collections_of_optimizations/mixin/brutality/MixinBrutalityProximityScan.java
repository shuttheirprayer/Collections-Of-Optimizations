package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BrutalityProximityScan;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Pseudo
@Mixin(targets = "net.goo.brutality.event.forge.client.ForgeClientPlayerStateHandler", remap = false)
public abstract class MixinBrutalityProximityScan {

    @Inject(
            method = "onClientTick",
            at = @At("HEAD"),
            require = 0
    )
    private static void coo$advanceProximityTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (event != null && event.phase == TickEvent.Phase.END) {
            BrutalityProximityScan.advance();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(
            method = "onClientTick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            ),
            require = 0
    )
    private static boolean coo$throttleProximityScan(Stream stream, Predicate predicate) {
        int interval = CoOConfig.brutalityProximityScanInterval;
        if (interval <= 1) {
            return stream.anyMatch(predicate);
        }
        return BrutalityProximityScan.query(stream, predicate, interval);
    }
}
