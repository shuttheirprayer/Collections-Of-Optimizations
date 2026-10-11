package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(BiomeManager.class)
public abstract class MixinBiomeManagerQuartCache {

    @Unique
    private long[] coo$quartKeys;

    @Unique
    private Holder<Biome>[] coo$quartValues;

    @Unique
    private int coo$quartStamp;

    @SuppressWarnings("unchecked")
    @Redirect(method = "getNoiseBiomeAtQuart(III)Lnet/minecraft/core/Holder;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/BiomeManager$NoiseBiomeSource;getNoiseBiome(III)Lnet/minecraft/core/Holder;"),
            require = 0)
    private Holder<Biome> coo$cacheQuartLookup(BiomeManager.NoiseBiomeSource source, int x, int y, int z) {
        if (!CoOConfig.vanillaCacheBiomeQuartLookups || !RenderSystem.isOnRenderThread()) {
            return source.getNoiseBiome(x, y, z);
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return source.getNoiseBiome(x, y, z);
        }

        int now = player.tickCount;
        Holder<Biome>[] values = this.coo$quartValues;
        if (values == null) {
            values = (Holder<Biome>[]) new Holder[512];
            this.coo$quartValues = values;
            this.coo$quartKeys = new long[512];
            this.coo$quartStamp = now;
        } else if (this.coo$quartStamp != now) {
            this.coo$quartStamp = now;
            Arrays.fill(values, null);
        }

        long key = BlockPos.asLong(x, y, z);
        int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 55);
        if (this.coo$quartKeys[slot] == key) {
            Holder<Biome> hit = values[slot];
            if (hit != null) {
                return hit;
            }
        }

        Holder<Biome> value = source.getNoiseBiome(x, y, z);
        this.coo$quartKeys[slot] = key;
        values[slot] = value;
        return value;
    }
}
