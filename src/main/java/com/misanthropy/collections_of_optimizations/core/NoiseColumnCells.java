package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public final class NoiseColumnCells {
    private static final int SLOTS = 8;

    private static final ThreadLocal<NoiseColumnCells> LOCAL = ThreadLocal.withInitial(NoiseColumnCells::new);

    private final Object[] owners = new Object[SLOTS];
    private final RandomState[] states = new RandomState[SLOTS];
    private final NoiseSettings[] settings = new NoiseSettings[SLOTS];
    private final int[] cellX = new int[SLOTS];
    private final int[] cellZ = new int[SLOTS];
    private final NoiseChunk[] chunks = new NoiseChunk[SLOTS];
    private int next;

    private NoiseColumnCells() {
    }

    public static NoiseChunk get(Object owner, RandomState state, NoiseSettings noiseSettings, int startX, int startZ,
                                 NoiseGeneratorSettings generatorSettings, Aquifer.FluidPicker fluidPicker) {
        NoiseColumnCells cells = LOCAL.get();
        for (int i = 0; i < SLOTS; i++) {
            if (cells.chunks[i] != null && cells.owners[i] == owner && cells.states[i] == state
                    && cells.cellX[i] == startX && cells.cellZ[i] == startZ && cells.settings[i].equals(noiseSettings)) {
                return cells.chunks[i];
            }
        }
        NoiseChunk chunk = new NoiseChunk(1, state, startX, startZ, noiseSettings, DensityFunctions.BeardifierMarker.INSTANCE,
                generatorSettings, fluidPicker, Blender.empty());
        chunk.initializeForFirstCellX();
        chunk.advanceCellX(0);
        int slot = cells.next;
        cells.next = (slot + 1) % SLOTS;
        cells.owners[slot] = owner;
        cells.states[slot] = state;
        cells.settings[slot] = noiseSettings;
        cells.cellX[slot] = startX;
        cells.cellZ[slot] = startZ;
        cells.chunks[slot] = chunk;
        return chunk;
    }
}
