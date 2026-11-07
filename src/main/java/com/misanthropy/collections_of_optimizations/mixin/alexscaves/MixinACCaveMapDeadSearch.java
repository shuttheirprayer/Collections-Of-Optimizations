package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.level.map.CaveBiomeMapWorldWorker;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CaveBiomeMapWorldWorker.class, remap = false)
public abstract class MixinACCaveMapDeadSearch {

    @Shadow private ServerLevel serverLevel;
    @Shadow private ResourceKey<Biome> biomeResourceKey;
    @Shadow private boolean complete;

    @Shadow
    public abstract void onWorkComplete(BlockPos biomeCorner);

    @Inject(method = "hasWork", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$endSearchForDisabledBiome(CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.alexscavesSkipDeadCaveMaps
                && !this.complete
                && BiomeGenerationConfig.isBiomeDisabledCompletely(this.biomeResourceKey)
                && !"alex_caves_dimensions".equals(this.serverLevel.dimension().location().getNamespace())) {
            this.onWorkComplete(null);
            cir.setReturnValue(false);
        }
    }
}
