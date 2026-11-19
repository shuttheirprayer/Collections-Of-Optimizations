package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunkPostProcessingRelease {
    @Inject(method = "postProcessGeneration", at = @At("TAIL"), require = 0)
    private void coo$releaseDrainedLists(CallbackInfo ci) {
        ShortList[] lists = ((ChunkAccess) (Object) this).getPostProcessing();
        for (int i = 0; i < lists.length; i++) {
            ShortList list = lists[i];
            if (list != null && list.isEmpty()) {
                lists[i] = null;
            }
        }
    }
}
