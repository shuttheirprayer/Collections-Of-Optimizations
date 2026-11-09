package com.misanthropy.collections_of_optimizations.mixin.justdirethings;

import com.direwolf20.justdirethings.client.events.RenderLevelLast;
import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.AreaPreviewChunkCache;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(value = RenderLevelLast.class, remap = false)
public abstract class MixinRenderLevelLast {

    @Inject(
            method = "renderAreaPreviews(Lnet/minecraftforge/client/event/RenderLevelStageEvent;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdleAreaPreviews(RenderLevelStageEvent evt, Player player, CallbackInfo ci) {
        if (CoOConfig.justdirethingsLeanAreaPreviewScan
                && AreaPreviewChunkCache.scannedAndEmpty(ClientTickStamp.current())) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderAreaPreviews(Lnet/minecraftforge/client/event/RenderLevelStageEvent;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;getBlockEntities()Ljava/util/Map;",
                    remap = true
            ),
            require = 0
    )
    private static Map<BlockPos, BlockEntity> coo$skipChunksWithoutAreaBlocks(LevelChunk chunk,
                                                                             Operation<Map<BlockPos, BlockEntity>> original) {
        if (!CoOConfig.justdirethingsLeanAreaPreviewScan) {
            return original.call(chunk);
        }

        long chunkKey = chunk.getPos().toLong();
        byte state = AreaPreviewChunkCache.state(ClientTickStamp.current(), chunkKey);
        if (state == AreaPreviewChunkCache.ABSENT) {
            return Collections.emptyMap();
        }

        Map<BlockPos, BlockEntity> blockEntities = original.call(chunk);
        if (state == AreaPreviewChunkCache.PRESENT) {
            return blockEntities;
        }

        boolean present = false;
        for (BlockEntity blockEntity : blockEntities.values()) {
            if (blockEntity instanceof AreaAffectingBE) {
                present = true;
                break;
            }
        }
        AreaPreviewChunkCache.record(chunkKey, present);
        return present ? blockEntities : Collections.emptyMap();
    }
}
