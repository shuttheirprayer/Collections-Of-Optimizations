package com.misanthropy.collections_of_optimizations.mixin.bmd;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BmdBlockCacheState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache", remap = false)
public abstract class MixinBmdChunkBlockCacheTracker {

    @Inject(method = "addToChunk", at = @At("HEAD"), require = 0)
    private void coo$trackAdd(ChunkPos chunkPos, Block block, BlockPos pos, CallbackInfo ci) {
        if (!coo$tracking()) {
            return;
        }
        BmdBlockCacheState.track(block, pos);
    }

    @Inject(method = "removeFromChunk", at = @At("HEAD"), require = 0)
    private void coo$trackRemove(ChunkPos chunkPos, Block block, BlockPos pos, CallbackInfo ci) {
        if (!coo$tracking()) {
            return;
        }
        BmdBlockCacheState.untrack(block, pos);
    }

    @Inject(method = "getBlocksFromChunk", at = @At("RETURN"), require = 0)
    private void coo$resyncFromLookup(ChunkPos chunkPos, Block block, CallbackInfoReturnable<List<BlockPos>> cir) {
        if (!coo$tracking()) {
            return;
        }
        List<BlockPos> found = cir.getReturnValue();
        if (found == null || found.isEmpty()) {
            return;
        }
        BmdBlockCacheState.trackAll(block, found);
    }

    private static boolean coo$tracking() {
        return CoOConfig.bmdLeanWardSpawnScan
                || CoOConfig.bmdLeanMonolithExplosionScan
                || CoOConfig.bmdLeanLevitationFlightScan;
    }
}
