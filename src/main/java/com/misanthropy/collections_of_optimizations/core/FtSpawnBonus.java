package com.misanthropy.collections_of_optimizations.core;

import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import net.kayn.fallen_traits.content.item.SpawnRateModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Arrays;

public final class FtSpawnBonus {

    private static final double RADIUS_SQR = 16384.0D;
    private static final double[] NONE = new double[0];
    private static final ThreadLocal<boolean[]> EXTRA_PASS = ThreadLocal.withInitial(() -> new boolean[1]);

    private FtSpawnBonus() {
    }

    public static void afterSpawn(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state,
                                  boolean spawnFriendlies, boolean spawnEnemies, boolean spawnPersistent) {
        if (!(level instanceof FtLevelState holder)) {
            return;
        }
        int tick = level.getServer().getTickCount();
        double[] bonuses = holder.coo$ftSpawnBonuses();
        if (bonuses == null || holder.coo$ftSpawnTick() != tick) {
            bonuses = snapshot(level);
            holder.coo$ftSetSpawnBonuses(tick, bonuses);
        }
        if (bonuses.length == 0) {
            return;
        }
        boolean[] extraPass = EXTRA_PASS.get();
        if (extraPass[0]) {
            return;
        }
        ChunkPos pos = chunk.getPos();
        double x = pos.getMiddleBlockX();
        double z = pos.getMiddleBlockZ();
        float bonus = 0.0F;
        for (int i = 0; i < bonuses.length; i += 3) {
            double dx = bonuses[i] - x;
            double dz = bonuses[i + 1] - z;
            if (!(dx * dx + dz * dz > RADIUS_SQR)) {
                bonus = Math.max(bonus, (float) bonuses[i + 2]);
            }
        }
        if (!(bonus <= 0.0F)) {
            int extraPasses = (int) Math.floor(bonus);
            float fractionalPass = bonus - (float) extraPasses;
            if (level.random.nextFloat() < fractionalPass) {
                extraPasses++;
            }
            if (extraPasses > 0) {
                extraPass[0] = true;
                try {
                    for (int i = 0; i < extraPasses; i++) {
                        NaturalSpawner.spawnForChunk(level, chunk, state, spawnFriendlies, spawnEnemies, spawnPersistent);
                    }
                } finally {
                    extraPass[0] = false;
                }
            }
        }
    }

    private static double[] snapshot(ServerLevel level) {
        double[] out = null;
        int count = 0;
        for (ServerPlayer player : level.players()) {
            float playerBonus = 0.0F;
            for (ItemStack stack : CurioCompat.getItems(player, s -> s.getItem() instanceof SpawnRateModifier)) {
                if (stack.getItem() instanceof SpawnRateModifier modifier) {
                    playerBonus += Math.max(0.0F, modifier.getSpawnRateMultiplier());
                }
            }
            if (playerBonus != 0.0F) {
                if (out == null) {
                    out = new double[6];
                } else if (count + 3 > out.length) {
                    out = Arrays.copyOf(out, out.length * 2);
                }
                out[count++] = player.getX();
                out[count++] = player.getZ();
                out[count++] = playerBonus;
            }
        }
        return out == null ? NONE : Arrays.copyOf(out, count);
    }
}
