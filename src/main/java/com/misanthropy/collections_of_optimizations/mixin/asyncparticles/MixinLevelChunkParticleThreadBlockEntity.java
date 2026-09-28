package com.misanthropy.collections_of_optimizations.mixin.asyncparticles;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import forge.fun.qu_an.minecraft.asyncparticles.client.config.MixinConfigHelper;
import forge.fun.qu_an.minecraft.asyncparticles.client.core.Diagnostic;
import forge.fun.qu_an.minecraft.asyncparticles.client.util.ThreadUtil;
import forge.fun.qu_an.minecraft.asyncparticles.client.util.TrackedWriteMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunkParticleThreadBlockEntity {

    @Shadow
    @Final
    Level level;

    @Unique
    private boolean coo$particleThreadRead() {
        return this.level.isClientSide && ThreadUtil.isOnParticleThread();
    }

    @Redirect(
            method = "getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0)
    )
    private Object coo$keepPendingOnParticleThread(Map<?, ?> pending, Object pos) {
        return coo$particleThreadRead() ? null : pending.remove(pos);
    }

    @ModifyExpressionValue(
            method = "getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;IMMEDIATE:Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;")
    )
    private LevelChunk.EntityCreationType coo$neverCreateOnParticleThread(LevelChunk.EntityCreationType immediate) {
        return coo$particleThreadRead() ? null : immediate;
    }

    @ModifyExpressionValue(
            method = "getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;isRemoved()Z")
    )
    private boolean coo$keepRemovedOnParticleThread(boolean removed) {
        return removed && !coo$particleThreadRead();
    }

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false)
    private void coo$trackBlockEntityWrites(CallbackInfo ci) {
        if (!this.level.isClientSide || MixinConfigHelper.isSafeBlockEntityMap()) {
            return;
        }
        ChunkAccessBlockEntityMapsAccessor maps = (ChunkAccessBlockEntityMapsAccessor) this;
        Map<BlockPos, BlockEntity> blockEntities = maps.coo$getBlockEntities();
        if (!(blockEntities instanceof TrackedWriteMap)) {
            maps.coo$setBlockEntities(new TrackedWriteMap<>(Diagnostic::illegalBlockEntityStorageAccess, blockEntities));
        }
        Map<BlockPos, CompoundTag> pending = maps.coo$getPendingBlockEntities();
        if (!(pending instanceof TrackedWriteMap)) {
            maps.coo$setPendingBlockEntities(new TrackedWriteMap<>(Diagnostic::illegalBlockEntityStorageAccess, pending));
        }
    }
}
