package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public final class BwgTerrainChunkFilter {

    private static final ResourceKey<Biome> CRAG_GARDENS =
            ResourceKey.create(Registries.BIOME, new ResourceLocation("biomeswevegone", "crag_gardens"));

    private static final ResourceKey<Biome> BASALT_BARRERA =
            ResourceKey.create(Registries.BIOME, new ResourceLocation("biomeswevegone", "basalt_barrera"));

    private static final Predicate<Holder<Biome>> IS_CRAG_GARDENS = holder -> holder.is(CRAG_GARDENS);

    private static final Predicate<Holder<Biome>> IS_BASALT_BARRERA = holder -> holder.is(BASALT_BARRERA);

    private static final ThreadLocal<WeakReference<WorldGenRegion>> SURFACE_REGION = new ThreadLocal<>();

    private BwgTerrainChunkFilter() {
    }

    public static void rememberSurfaceRegion(WorldGenRegion region) {
        SURFACE_REGION.set(new WeakReference<>(region));
    }

    public static boolean hasCragGardens(ChunkAccess chunk) {
        WeakReference<WorldGenRegion> region = SURFACE_REGION.get();
        return near(region == null ? null : region.get(), chunk, IS_CRAG_GARDENS);
    }

    public static boolean hasBasaltBarrera(WorldGenRegion region, ChunkAccess chunk) {
        return near(region, chunk, IS_BASALT_BARRERA);
    }

    private static boolean near(WorldGenRegion region, ChunkAccess chunk, Predicate<Holder<Biome>> predicate) {
        ChunkPos pos = chunk.getPos();
        if (region == null || !pos.equals(region.getCenter()) || region.getChunk(pos.x, pos.z, ChunkStatus.EMPTY, false) != chunk) {
            return true;
        }
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                ChunkAccess neighbour = dx == 0 && dz == 0
                        ? chunk
                        : region.getChunk(pos.x + dx, pos.z + dz, ChunkStatus.BIOMES, false);
                if (neighbour == null || contains(neighbour, predicate)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean contains(ChunkAccess chunk, Predicate<Holder<Biome>> predicate) {
        LevelChunkSection[] sections;
        try {
            sections = chunk.getSections();
        } catch (Throwable ignored) {
            return true;
        }
        if (sections == null || sections.length == 0) {
            return true;
        }
        for (LevelChunkSection section : sections) {
            if (section != null && section.getBiomes().maybeHas(predicate)) {
                return true;
            }
        }
        return false;
    }
}
