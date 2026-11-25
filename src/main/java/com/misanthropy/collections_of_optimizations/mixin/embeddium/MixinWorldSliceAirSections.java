package com.misanthropy.collections_of_optimizations.mixin.embeddium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldSlice.class, remap = false)
public abstract class MixinWorldSliceAirSections {

    @Unique
    private final boolean[] coo$air = new boolean[64];

    @Shadow
    private void unpackBlockData(BlockState[] blockArray, ChunkRenderContext context, ClonedChunkSection section) {
        throw new AssertionError();
    }

    @Redirect(
            method = "copySectionData",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;unpackBlockData([Lnet/minecraft/world/level/block/state/BlockState;Lme/jellysquid/mods/sodium/client/world/cloned/ChunkRenderContext;Lme/jellysquid/mods/sodium/client/world/cloned/ClonedChunkSection;)V")
    )
    private void coo$keepAir(WorldSlice slice, BlockState[] blockArray, ChunkRenderContext context, ClonedChunkSection section, ChunkRenderContext sameContext, int sectionIndex) {
        boolean air = section.getBlockData() == null;
        if (air && this.coo$air[sectionIndex] && CoOConfig.embeddiumKeepAirSections) {
            return;
        }
        this.coo$air[sectionIndex] = false;
        this.unpackBlockData(blockArray, context, section);
        this.coo$air[sectionIndex] = air;
    }
}
