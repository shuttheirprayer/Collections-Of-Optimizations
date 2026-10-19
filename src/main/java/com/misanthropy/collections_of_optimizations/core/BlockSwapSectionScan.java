package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.blockswap.PalettedContainerDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.IntConsumer;

public final class BlockSwapSectionScan {

    private static final int MAX_PALETTE = 256;

    private BlockSwapSectionScan() {
    }

    public static boolean sweep(LevelChunkSection section, Map<BlockState, BlockState[]> plan, Level world, int baseX, int baseY, int baseZ) {
        BlockState[] tracked;
        BitStorage storage;
        try {
            PalettedContainer.Data<BlockState> data = ((PalettedContainerDataAccessor<BlockState>) section.getStates()).coo$data();
            Palette<BlockState> palette = data.palette();
            int size = palette.getSize();
            if (size > MAX_PALETTE) {
                return false;
            }
            tracked = null;
            for (int id = 0; id < size; id++) {
                BlockState state = palette.valueFor(id);
                if (state != null && plan.containsKey(state)) {
                    if (tracked == null) {
                        tracked = new BlockState[size];
                    }
                    tracked[id] = state;
                }
            }
            storage = data.storage();
        } catch (RuntimeException e) {
            return false;
        }
        if (tracked == null) {
            return true;
        }
        Hits hits = new Hits(tracked);
        storage.getAll(hits);
        for (int i = 0; i < hits.count; i++) {
            int index = hits.indices[i];
            BlockState[] swap = plan.get(hits.states[i]);
            BlockPos pos = new BlockPos(baseX + (index & 15), baseY + (index >> 8), baseZ + (index >> 4 & 15));
            if (swap[0] != null) {
                world.setBlock(pos, swap[0], 2);
            }
            if (swap[1] != null) {
                world.setBlock(pos, swap[1], 2);
            }
        }
        return true;
    }

    private static final class Hits implements IntConsumer {

        private final BlockState[] tracked;
        private int[] indices = new int[16];
        private BlockState[] states = new BlockState[16];
        private int count;
        private int next;

        private Hits(BlockState[] tracked) {
            this.tracked = tracked;
        }

        @Override
        public void accept(int id) {
            int index = this.next++;
            if (id >= this.tracked.length) {
                return;
            }
            BlockState state = this.tracked[id];
            if (state == null) {
                return;
            }
            if (this.count == this.indices.length) {
                this.indices = Arrays.copyOf(this.indices, this.count << 1);
                this.states = Arrays.copyOf(this.states, this.count << 1);
            }
            this.indices[this.count] = index;
            this.states[this.count++] = state;
        }
    }
}
