package com.misanthropy.collections_of_optimizations.mixin.skyarena;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.jrdemiurge.skyarena.block.entity.AltarBlockEntity", remap = false)
public abstract class MixinAltarSpawnScan {

    @Shadow
    private int mobSpawnRadius;

    @Shadow
    private int spawnDistanceFromPlayer;

    @Shadow
    private boolean allowWaterAndAirSpawn;

    @Shadow
    private boolean battlePhaseActive;

    @Unique
    private List<BlockPos> coo$spawnCache;

    @Unique
    private long coo$spawnCacheTick = Long.MIN_VALUE;

    @Unique
    private long coo$spawnCacheCenter;

    @Unique private int coo$spawnCacheOwner = -1;

    @Inject(method = "findValidSpawnPositions", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$leanSpawnScan(Level level, BlockPos center, Player player, CallbackInfoReturnable<List<BlockPos>> cir) {
        if (!CoOConfig.skyarenaLeanSpawnScan) {
            return;
        }
        if (level == null || center == null || player == null || level.isClientSide) {
            return;
        }

        long now = level.getGameTime();
        long centerKey = center.asLong();
        int owner = player.getId();
        boolean cacheable = this.battlePhaseActive;

        if (cacheable) {
            List<BlockPos> cached = this.coo$spawnCache;
            if (cached != null && this.coo$spawnCacheCenter == centerKey && this.coo$spawnCacheOwner == owner) {
                long age = now - this.coo$spawnCacheTick;
                if (age >= 0L && age <= CoOConfig.skyarenaSpawnScanCacheTicks) {
                    cir.setReturnValue(cached);
                    return;
                }
            }
        }

        List<BlockPos> found = this.coo$scanSpawnPositions(level, center, player);

        if (cacheable) {
            this.coo$spawnCache = found;
            this.coo$spawnCacheTick = now;
            this.coo$spawnCacheCenter = centerKey;
            this.coo$spawnCacheOwner = owner;
        } else {
            this.coo$spawnCache = null;
            this.coo$spawnCacheTick = Long.MIN_VALUE;
            this.coo$spawnCacheOwner = -1;
        }

        cir.setReturnValue(found);
    }

    @Unique
    private List<BlockPos> coo$scanSpawnPositions(Level level, BlockPos center, Player player) {
        List<BlockPos> found = new ArrayList<>();

        int radius = this.mobSpawnRadius;
        if (radius < 0) {
            return found;
        }

        int radiusSq = radius * radius;
        int minDistanceSq = this.spawnDistanceFromPlayer * this.spawnDistanceFromPlayer;
        boolean loose = this.allowWaterAndAirSpawn;

        BlockPos playerPos = player.blockPosition();
        double playerX = playerPos.getX();
        double playerY = playerPos.getY();
        double playerZ = playerPos.getZ();

        int centerX = center.getX();
        int centerY = center.getY();
        int centerZ = center.getZ();

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            int xSq = x * x;
            for (int z = -radius; z <= radius; z++) {
                if (xSq + z * z > radiusSq) {
                    continue;
                }

                int blockX = centerX + x;
                int blockZ = centerZ + z;

                double dx = playerX - blockX;
                double dy = playerY - centerY;
                double dz = playerZ - blockZ;
                if (dx * dx + dy * dy + dz * dz <= minDistanceSq) {
                    continue;
                }

                if (loose) {
                    if (!coo$isPassable(level, cursor.set(blockX, centerY, blockZ))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX, centerY + 1, blockZ))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX, centerY + 2, blockZ))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX, centerY, blockZ - 1))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX, centerY, blockZ + 1))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX + 1, centerY, blockZ))) {
                        continue;
                    }
                    if (!coo$isPassable(level, cursor.set(blockX - 1, centerY, blockZ))) {
                        continue;
                    }
                } else {
                    if (!level.isEmptyBlock(cursor.set(blockX, centerY, blockZ))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX, centerY + 1, blockZ))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX, centerY + 2, blockZ))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX, centerY, blockZ - 1))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX, centerY, blockZ + 1))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX + 1, centerY, blockZ))) {
                        continue;
                    }
                    if (!level.isEmptyBlock(cursor.set(blockX - 1, centerY, blockZ))) {
                        continue;
                    }
                    if (level.isEmptyBlock(cursor.set(blockX, centerY - 1, blockZ))) {
                        continue;
                    }
                }

                found.add(new BlockPos(blockX, centerY, blockZ));
            }
        }

        return found;
    }

    @Unique
    private static boolean coo$isPassable(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }
}
