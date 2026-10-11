package com.misanthropy.collections_of_optimizations.mixin.blockswap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.swapper.Swapper;
import corgitaco.blockswap.util.TickHelper;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = Swapper.class, remap = false)
public abstract class MixinSwapper {

    @Inject(
            method = "runRetroGenerator(Lnet/minecraft/world/level/Level;[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$fastRetroGenerator(Level world, LevelChunkSection[] sections, LevelChunk chunk, CallbackInfo ci) {
        if (!CoOConfig.blockswapPaletteFilteredRetroGen) {
            return;
        }
        ci.cancel();

        BlockSwapConfig config = BlockSwapConfig.getConfig(false);
        TickHelper tickHelper = (TickHelper) chunk;
        if (!config.retroGen() || tickHelper.markTickDirty()) {
            return;
        }

        Map<Block, Block> blockMap = config.blockBlockMap();
        Map<BlockState, BlockState> stateMap = config.blockStateBlockStateMap();
        if (!blockMap.isEmpty() || !stateMap.isEmpty()) {
            coo$sweep(world, sections, chunk, config, blockMap, stateMap);
        }

        tickHelper.setTickDirty();
    }

    @Unique
    private static BlockSwapConfig coo$planConfig;

    @Unique
    private static Reference2ObjectOpenHashMap<BlockState, BlockState[]> coo$plan;

    @Unique
    private static Reference2ObjectOpenHashMap<BlockState, BlockState[]> coo$plan(BlockSwapConfig config, Map<Block, Block> blockMap,
                                                                                  Map<BlockState, BlockState> stateMap) {
        Reference2ObjectOpenHashMap<BlockState, BlockState[]> plan = coo$plan;
        if (plan != null && coo$planConfig == config) {
            return plan;
        }
        plan = new Reference2ObjectOpenHashMap<>();
        for (Block block : blockMap.keySet()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                plan.put(state, new BlockState[]{Swapper.remapState(state), stateMap.get(state)});
            }
        }
        for (Map.Entry<BlockState, BlockState> entry : stateMap.entrySet()) {
            if (!plan.containsKey(entry.getKey())) {
                plan.put(entry.getKey(), new BlockState[]{null, entry.getValue()});
            }
        }
        coo$plan = plan;
        coo$planConfig = config;
        return plan;
    }

    @Unique
    private static void coo$sweep(Level world, LevelChunkSection[] sections, LevelChunk chunk, BlockSwapConfig config,
                                  Map<Block, Block> blockMap, Map<BlockState, BlockState> stateMap) {
        int baseX = SectionPos.sectionToBlockCoord(chunk.getPos().x);
        int baseZ = SectionPos.sectionToBlockCoord(chunk.getPos().z);

        Reference2ObjectOpenHashMap<BlockState, BlockState[]> plan = coo$plan(config, blockMap, stateMap);
        Predicate<BlockState> tracked = plan::containsKey;

        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section == null || !section.maybeHas(tracked)) {
                continue;
            }

            int baseY = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(index));
            PalettedContainer<BlockState> states = section.getStates();

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState[] swap = plan.get(states.get(x, y, z));
                        if (swap == null) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                        if (swap[0] != null) {
                            world.setBlock(pos, swap[0], 2);
                        }
                        if (swap[1] != null) {
                            world.setBlock(pos, swap[1], 2);
                        }
                    }
                }
            }
        }
    }
}
