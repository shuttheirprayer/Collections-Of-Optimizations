package com.misanthropy.collections_of_optimizations.mixin.chunksending;

import com.bawnorton.mixinsquared.TargetHandler;
import com.chunksending.chunk.IChunkPacketCache;
import com.chunksending.event.EventHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkMap.class, priority = 1500)
public abstract class MixinChunkSendingSharedCache {

    @TargetHandler(
            mixin = "com.chunksending.mixin.cache.ChunkMapPacketCacheMixin",
            name = "chunksending$cachePacket"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lcom/chunksending/chunk/IChunkPacketCache;setCachedPacket(Lnet/minecraft/network/protocol/game/ClientboundLevelChunkWithLightPacket;)V"),
            require = 0,
            remap = false
    )
    private void coo$cachePacketWhenShared(IChunkPacketCache cache, ClientboundLevelChunkWithLightPacket packet, @Local(argsOnly = true) ServerPlayer player) {
        if (coo$shared(player)) {
            cache.setCachedPacket(packet);
        }
    }

    @TargetHandler(
            mixin = "com.chunksending.mixin.cache.ChunkMapPacketCacheMixin",
            name = "chunksending$cachePacket"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lcom/chunksending/event/EventHandler;addToClear(Lcom/chunksending/chunk/IChunkPacketCache;)V"),
            require = 0,
            remap = false
    )
    private void coo$expireWhenShared(IChunkPacketCache cache, @Local(argsOnly = true) ServerPlayer player) {
        if (coo$shared(player)) {
            EventHandler.addToClear(cache);
        }
    }

    @Unique
    private static boolean coo$shared(ServerPlayer player) {
        return !CoOConfig.chunksendingCacheOnlyWhenShared || player.server.getPlayerCount() > 1;
    }
}
