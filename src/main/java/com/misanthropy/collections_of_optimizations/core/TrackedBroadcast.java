package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class TrackedBroadcast {

    private static ResourceKey<Level> lastDimension;

    private TrackedBroadcast() {
    }

    public static boolean send(SimpleChannel channel, Object message, int entityId) {
        if (entityId < 0) {
            return false;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isSameThread()) {
            return false;
        }
        ServerLevel level = lastDimension == null ? null : server.getLevel(lastDimension);
        Entity entity = level == null ? null : level.getEntity(entityId);
        if (entity == null) {
            for (ServerLevel candidate : server.getAllLevels()) {
                entity = candidate.getEntity(entityId);
                if (entity != null) {
                    level = candidate;
                    lastDimension = candidate.dimension();
                    break;
                }
            }
            if (entity == null) {
                return false;
            }
        }
        level.getChunkSource().broadcastAndSend(entity, channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
        return true;
    }
}
