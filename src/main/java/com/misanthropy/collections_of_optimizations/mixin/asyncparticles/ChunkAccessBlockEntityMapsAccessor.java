package com.misanthropy.collections_of_optimizations.mixin.asyncparticles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ChunkAccess.class)
public interface ChunkAccessBlockEntityMapsAccessor {

    @Accessor("blockEntities")
    Map<BlockPos, BlockEntity> coo$getBlockEntities();

    @Accessor("blockEntities")
    @Mutable
    void coo$setBlockEntities(Map<BlockPos, BlockEntity> blockEntities);

    @Accessor("pendingBlockEntities")
    Map<BlockPos, CompoundTag> coo$getPendingBlockEntities();

    @Accessor("pendingBlockEntities")
    @Mutable
    void coo$setPendingBlockEntities(Map<BlockPos, CompoundTag> pendingBlockEntities);
}
