package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CaveBiomeQuadCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ACBiomeRarity.class, remap = false)
public abstract class MixinACBiomeRarity {

    @Inject(
            method = "getRareBiomeInfoForQuad",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$serveMemoisedQuad(long worldSeed, int x, int z,
                                              CallbackInfoReturnable<VoronoiGenerator.VoronoiInfo> cir) {
        if (!CoOConfig.alexscavesMemoRareBiomeQuads) {
            return;
        }
        VoronoiGenerator.VoronoiInfo hit = CaveBiomeQuadCache.get().lookup(worldSeed, AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get(), x, z);
        if (hit != null) {
            cir.setReturnValue(hit == CaveBiomeQuadCache.ABSENT ? null : hit);
        }
    }

    @Inject(
            method = "getRareBiomeInfoForQuad",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeQuad(long worldSeed, int x, int z,
                                      CallbackInfoReturnable<VoronoiGenerator.VoronoiInfo> cir) {
        if (!CoOConfig.alexscavesMemoRareBiomeQuads) {
            return;
        }
        CaveBiomeQuadCache.get().store(worldSeed, AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get(), x, z, cir.getReturnValue());
    }

    @Inject(
            method = "init",
            at = @At("HEAD"),
            require = 0
    )
    private static void coo$invalidateOnInit(CallbackInfo ci) {
        CaveBiomeQuadCache.invalidate();
    }
}
