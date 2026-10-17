package com.misanthropy.collections_of_optimizations.worldgen.iafden;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IafDenGenFlag;
import com.misanthropy.collections_of_optimizations.core.IafDenRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

public class IafDenPiece extends StructurePiece {

    private static final int GEN_RADIUS = 48;

    private final IafDenType denType;
    private boolean generated;

    public IafDenPiece(BlockPos pos, IafDenType denType) {
        super(IafDenRegistry.DEN_PIECE_TYPE.get(), 0, new BoundingBox(
                pos.getX() - GEN_RADIUS, pos.getY() - GEN_RADIUS, pos.getZ() - GEN_RADIUS,
                pos.getX() + GEN_RADIUS, pos.getY() + GEN_RADIUS, pos.getZ() + GEN_RADIUS));
        this.denType = denType;
    }

    public IafDenPiece(CompoundTag tag) {
        super(IafDenRegistry.DEN_PIECE_TYPE.get(), tag);
        this.denType = IafDenType.valueOf(tag.getString("CooDenType"));
        this.generated = tag.getBoolean("CooGenerated");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tag) {
        tag.putString("CooDenType", this.denType.name());
        tag.putBoolean("CooGenerated", this.generated);
    }

    @Override
    public void postProcess(
            @NotNull WorldGenLevel level,
            @NotNull StructureManager structureManager,
            @NotNull ChunkGenerator chunkGenerator,
            @NotNull RandomSource random,
            @NotNull BoundingBox box,
            @NotNull ChunkPos chunkPos,
            @NotNull BlockPos pos) {
        if (this.generated || !CoOConfig.iafdragonfixStructureDens || !IafDenRegistry.isPortActive()) {
            return;
        }
        BlockPos center = this.boundingBox.getCenter();
        if (chunkPos.x != SectionPos.blockToSectionCoord(center.getX())
                || chunkPos.z != SectionPos.blockToSectionCoord(center.getZ())) {
            return;
        }
        this.generated = true;

        ServerLevel serverLevel = level.getLevel();
        BlockPos spawn = serverLevel.getSharedSpawnPos();
        double distSq = spawn.distSqr(new BlockPos(center.getX(), spawn.getY(), center.getZ()));
        int minDist = this.denType.isCave()
                ? CoOConfig.iafdragonfixCaveSpawnDistance
                : CoOConfig.iafdragonfixRoostSpawnDistance;
        if (minDist > 0 && distSq < (double) minDist * (double) minDist) {
            return;
        }

        try {
            IafDenGenFlag.enable();
            IafDenGen.generate(this.denType, center, level, chunkGenerator, random);
        } catch (Exception | LinkageError exception) {
            IafDenRegistry.LOGGER.error("Failed to generate dragon den {}", this.denType, exception);
        } finally {
            IafDenGenFlag.disable();
        }
    }
}
