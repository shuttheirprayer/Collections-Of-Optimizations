package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class FdBossesClientPlayer {

    private FdBossesClientPlayer() {
    }

    public static boolean isLocalPlayer(Player player) {
        return Minecraft.getInstance().player == player;
    }
}
