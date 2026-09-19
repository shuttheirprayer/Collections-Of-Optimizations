package com.misanthropy.collections_of_optimizations.mixin.species;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.ninni.species.client.events.ForgeClientEvents", remap = false)
public abstract class MixinSpeciesRenderListener {

    @Inject(method = "livingEntityRenderer", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$onlyHandleRenderPre(RenderLivingEvent<?, ?> event, CallbackInfo ci) {
        if (CoOConfig.speciesFixDoubleRenderListener && !(event instanceof RenderLivingEvent.Pre)) {
            ci.cancel();
        }
    }
}
