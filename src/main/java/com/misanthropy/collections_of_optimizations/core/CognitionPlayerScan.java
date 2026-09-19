package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CognitionPlayerScan {

    private CognitionPlayerScan() {
    }

    public static List<Player> playersIn(Level level, AABB box) {
        List<? extends Player> players = level.players();
        int size = players.size();
        List<Player> found = null;
        for (int i = 0; i < size; i++) {
            Player player = players.get(i);
            if (player == null || player.isSpectator()) {
                continue;
            }
            if (!player.getBoundingBox().intersects(box)) {
                continue;
            }
            if (found == null) {
                found = new ArrayList<>(2);
            }
            found.add(player);
        }
        return found == null ? Collections.emptyList() : found;
    }
}
