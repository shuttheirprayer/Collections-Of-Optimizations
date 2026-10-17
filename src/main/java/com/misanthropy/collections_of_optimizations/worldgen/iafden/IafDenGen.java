package com.misanthropy.collections_of_optimizations.worldgen.iafden;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.util.HomePosition;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.github.alexthe666.iceandfire.world.gen.WorldGenDragonCave;
import com.github.alexthe666.iceandfire.world.gen.WorldGenDragonRoosts;
import com.misanthropy.collections_of_optimizations.mixin.iafdragonfix.WorldGenDragonRoostsInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Optional;

final class IafDenGen {

    private IafDenGen() {
    }

    static void generate(IafDenType denType, BlockPos center, WorldGenLevel level,
                         ChunkGenerator chunkGenerator, RandomSource random) {
        if (denType.isCave()) {
            generateCave(denType, center, level, random);
        } else {
            generateRoost(denType, center, level, chunkGenerator, random);
        }
    }

    private static void generateCave(IafDenType denType, BlockPos center, WorldGenLevel level, RandomSource random) {
        WorldGenDragonCave feature = caveFeature(denType);
        if (feature == null) {
            return;
        }
        ChunkPos chunkPos = new ChunkPos(center);
        BlockPos origin = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        int y = 40;
        for (int dx = 0; dx < 20; dx++) {
            for (int dz = 0; dz < 20; dz++) {
                y = Math.min(y, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX() + dx, origin.getZ() + dz));
            }
        }
        y -= 20;
        y -= random.nextInt(30);
        if (y < level.getMinBuildHeight() + 20) {
            return;
        }

        BlockPos cavePos = new BlockPos(chunkPos.x * 16 + 8, y, chunkPos.z * 16 + 8);
        boolean isMale = random.nextBoolean();
        feature.isMale = isMale;
        int dragonAge = 75 + random.nextInt(50);
        int radius = (int) ((float) dragonAge * 0.2F) + random.nextInt(4);
        feature.generateCave(level, radius, 3, cavePos, random);
        spawnCaveDragon(level, random, cavePos, dragonAge, isMale, feature);
    }

    private static void spawnCaveDragon(
            WorldGenLevel level, RandomSource random, BlockPos pos, int age, boolean isMale, WorldGenDragonCave feature) {
        EntityType<? extends EntityDragonBase> entityType = feature.getDragonType();
        ServerLevel serverLevel = level.getLevel();
        EntityDragonBase dragon = entityType.create(serverLevel);
        if (dragon == null) {
            return;
        }
        dragon.setGender(isMale);
        dragon.growDragon(age);
        dragon.setAgingDisabled(true);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.setVariant(random.nextInt(4));
        dragon.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
        dragon.setPersistenceRequired();
        dragon.homePos = new HomePosition(pos, serverLevel);
        dragon.setHunger(50);
        level.addFreshEntity(dragon);
    }

    private static void generateRoost(IafDenType denType, BlockPos origin, WorldGenLevel level,
                                      ChunkGenerator chunkGenerator, RandomSource random) {
        WorldGenDragonRoosts feature = roostFeature(denType);
        if (feature == null) {
            return;
        }
        FeaturePlaceContext<NoneFeatureConfiguration> context = new FeaturePlaceContext<>(
                Optional.empty(), level, chunkGenerator, random, origin, NoneFeatureConfiguration.INSTANCE);
        WorldGenDragonRoostsInvoker stages = (WorldGenDragonRoostsInvoker) feature;
        boolean isMale = random.nextBoolean();
        int size = 12 + random.nextInt(8);
        stages.coo$spawnDragon(context, size, isMale);
        stages.coo$generateSurface(context, size);
        stages.coo$generateShell(context, size);
        stages.coo$hollowOut(context, size - 2);
        stages.coo$generateDecoration(context, size + 15 - 2, isMale);
    }

    private static WorldGenDragonCave caveFeature(IafDenType denType) {
        return switch (denType) {
            case FIRE_CAVE -> (WorldGenDragonCave) IafWorldRegistry.FIRE_DRAGON_CAVE.get();
            case ICE_CAVE -> (WorldGenDragonCave) IafWorldRegistry.ICE_DRAGON_CAVE.get();
            case LIGHTNING_CAVE -> (WorldGenDragonCave) IafWorldRegistry.LIGHTNING_DRAGON_CAVE.get();
            default -> null;
        };
    }

    private static WorldGenDragonRoosts roostFeature(IafDenType denType) {
        return switch (denType) {
            case FIRE_ROOST -> (WorldGenDragonRoosts) IafWorldRegistry.FIRE_DRAGON_ROOST.get();
            case ICE_ROOST -> (WorldGenDragonRoosts) IafWorldRegistry.ICE_DRAGON_ROOST.get();
            case LIGHTNING_ROOST -> (WorldGenDragonRoosts) IafWorldRegistry.LIGHTNING_DRAGON_ROOST.get();
            default -> null;
        };
    }
}
