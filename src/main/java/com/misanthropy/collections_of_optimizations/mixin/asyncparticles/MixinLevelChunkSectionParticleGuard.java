package com.misanthropy.collections_of_optimizations.mixin.asyncparticles;

import forge.fun.qu_an.minecraft.asyncparticles.client.util.ThreadUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunkSection.class)
public abstract class MixinLevelChunkSectionParticleGuard {

    @Shadow @Final private PalettedContainer<BlockState> states;
    @Shadow private PalettedContainerRO<Holder<Biome>> biomes;

    @Overwrite
    public BlockState getBlockState(int x, int y, int z) {
        BlockState state;
        try {
            state = this.states.get(x, y, z);
        } catch (NullPointerException | MissingPaletteEntryException e) {
            if (ThreadUtil.isOnParticleThread()) {
                return Blocks.AIR.defaultBlockState();
            }
            throw e;
        }
        return state == null && ThreadUtil.isOnParticleThread() ? Blocks.AIR.defaultBlockState() : state;
    }

    @Overwrite
    public FluidState getFluidState(int x, int y, int z) {
        FluidState fluid;
        try {
            fluid = this.states.get(x, y, z).getFluidState();
        } catch (NullPointerException | MissingPaletteEntryException e) {
            if (ThreadUtil.isOnParticleThread()) {
                return Fluids.EMPTY.defaultFluidState();
            }
            throw e;
        }
        return fluid == null && ThreadUtil.isOnParticleThread() ? Fluids.EMPTY.defaultFluidState() : fluid;
    }

    @Overwrite
    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        Holder<Biome> biome;
        try {
            biome = this.biomes.get(x, y, z);
        } catch (NullPointerException | MissingPaletteEntryException e) {
            if (ThreadUtil.isOnParticleThread()) {
                return coo$plains();
            }
            throw e;
        }
        return biome == null && ThreadUtil.isOnParticleThread() ? coo$plains() : biome;
    }

    private static Holder<Biome> coo$plains() {
        return Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
    }
}
