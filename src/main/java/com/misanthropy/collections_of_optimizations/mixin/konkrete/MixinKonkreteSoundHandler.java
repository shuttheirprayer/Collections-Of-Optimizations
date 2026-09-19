package com.misanthropy.collections_of_optimizations.mixin.konkrete;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import de.keksuccino.konkrete.sound.SoundHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoundHandler.class, remap = false)
public class MixinKonkreteSoundHandler {

    @Unique
    private static boolean coo$pollerWanted;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$deferVolumePoller(CallbackInfo ci) {
        if (CoOConfig.konkreteDeferSoundVolumeThread && !coo$pollerWanted) {
            ci.cancel();
        }
    }

    @Inject(method = "registerSound", at = @At("HEAD"), require = 0)
    private static void coo$startVolumePollerOnDemand(String key, String path, CallbackInfo ci) {
        if (CoOConfig.konkreteDeferSoundVolumeThread && !coo$pollerWanted && FMLEnvironment.dist == Dist.CLIENT) {
            coo$pollerWanted = true;
            SoundHandler.init();
        }
    }
}
