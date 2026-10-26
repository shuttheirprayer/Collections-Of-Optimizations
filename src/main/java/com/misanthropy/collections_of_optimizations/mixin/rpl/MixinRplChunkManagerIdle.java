package com.misanthropy.collections_of_optimizations.mixin.rpl;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.ritchiesprojectilelib.chunkloading.ChunkManager;

@Mixin(value = ChunkManager.class, remap = false)
public abstract class MixinRplChunkManagerIdle {

    @Shadow @Final private LongArrayFIFOQueue queue;
    @Shadow @Final private Long2IntOpenHashMap loaded;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipIdleTick(ServerLevel level, CallbackInfo ci) {
        if (CoOConfig.rplSkipIdleChunkTick && this.queue.isEmpty() && this.loaded.isEmpty()) {
            ci.cancel();
        }
    }
}
