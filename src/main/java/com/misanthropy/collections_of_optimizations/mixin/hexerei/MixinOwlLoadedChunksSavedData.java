package com.misanthropy.collections_of_optimizations.mixin.hexerei;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.HexereiOwlTickCache;
import com.misanthropy.collections_of_optimizations.core.OwlChunkDataProbe;
import net.joefoxe.hexerei.data.owl.OwlLoadedChunksSavedData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(value = OwlLoadedChunksSavedData.class, remap = false)
public abstract class MixinOwlLoadedChunksSavedData implements OwlChunkDataProbe {

    @Shadow
    Map<ResourceKey<Level>, Map<ChunkPos, List<UUID>>> chunkData;

    @Override
    public boolean coo$noOwlChunks() {
        return this.chunkData.isEmpty();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipEmptyTick(ServerLevel serverLevel, CallbackInfo ci) {
        if (!CoOConfig.hexereiSkipEmptyOwlChunkTick) {
            return;
        }
        HexereiOwlTickCache.remember(serverLevel.getDataStorage(), this);
        if (this.chunkData.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "serverTickEvent", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipLookup(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.hexereiSkipEmptyOwlChunkTick) {
            return;
        }
        if (event.phase != TickEvent.Phase.START) {
            ci.cancel();
            return;
        }
        OwlChunkDataProbe probe = HexereiOwlTickCache.find(event.getServer().overworld().getDataStorage());
        if (probe != null && probe.coo$noOwlChunks()) {
            ci.cancel();
        }
    }
}
