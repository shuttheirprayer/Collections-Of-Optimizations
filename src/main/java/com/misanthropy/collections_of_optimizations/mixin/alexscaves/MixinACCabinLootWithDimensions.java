package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.UndergroundCabinStructurePiece;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = UndergroundCabinStructurePiece.class, remap = false)
public abstract class MixinACCabinLootWithDimensions {

    @Inject(method = "pickBiome", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$pickAnyCaveWithDimensions(RandomSource randomSource, CallbackInfoReturnable<ResourceKey<Biome>> cir) {
        if (CoOConfig.alexscavesCabinLootWithDimensions && ModList.get().isLoaded("alex_caves_dimensions")) {
            cir.setReturnValue(Util.getRandom(ACBiomeRegistry.ALEXS_CAVES_BIOMES, randomSource));
        }
    }
}
