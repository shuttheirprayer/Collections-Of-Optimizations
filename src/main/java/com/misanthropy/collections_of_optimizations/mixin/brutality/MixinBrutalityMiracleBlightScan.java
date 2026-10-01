package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BrutalityMiracleBlight;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.goo.brutality.event.forge.client.ForgeClientParticleHandler", remap = false)
public abstract class MixinBrutalityMiracleBlightScan {

    @Inject(method = "spawnMiracleBlightParticles", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipWhenNobodyBlighted(Player player, Level level, CallbackInfo ci) {
        if (CoOConfig.brutalitySkipIdleBlightScan && BrutalityMiracleBlight.none()) {
            ci.cancel();
        }
    }
}
