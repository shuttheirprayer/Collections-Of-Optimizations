package com.misanthropy.collections_of_optimizations.mixin.ftbchunks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.client.map.MapTask;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FTBChunksClient.class, remap = false)
public abstract class MixinFTBChunksMapWriting {

    @Inject(
            method = "queueOrExecute",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipMapTask(MapTask task, CallbackInfo ci) {
        if (CoOConfig.ftbchunksDisableMapWriting) {
            ci.cancel();
        }
    }

    @Inject(
            method = "rerender",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipRerender(BlockPos pos, CallbackInfo ci) {
        if (CoOConfig.ftbchunksDisableMapWriting) {
            ci.cancel();
        }
    }
}
