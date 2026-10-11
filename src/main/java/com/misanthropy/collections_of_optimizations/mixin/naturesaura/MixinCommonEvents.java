package com.misanthropy.collections_of_optimizations.mixin.naturesaura;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.AuraChunkHolder;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.events.CommonEvents;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommonEvents.class, remap = false)
public abstract class MixinCommonEvents {

    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "onLevelTick(Lnet/minecraftforge/event/TickEvent$LevelTickEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;"
            ),
            require = 0
    )
    private LazyOptional<IAuraChunk> coo$cachedAuraChunkCap(
            LevelChunk chunk,
            Capability<IAuraChunk> capability,
            Direction side,
            Operation<LazyOptional<IAuraChunk>> original) {
        if (!CoOConfig.naturesauraFastAuraChunkSweep
                || side != null
                || capability != NaturesAuraAPI.CAP_AURA_CHUNK
                || !(chunk instanceof AuraChunkHolder holder)) {
            return original.call(chunk, capability, side);
        }
        return (LazyOptional<IAuraChunk>) holder.coo$auraChunkCap();
    }

    @Inject(
            method = "handleChunkWatchDeferred(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ChunkPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$dropUnloadedWatchedChunk(Player player, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.naturesauraResolveWatchedChunks
                && player.level().getChunkSource() instanceof ServerChunkCache cache
                && cache.getChunkNow(pos.x, pos.z) == null) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "handleChunkWatchDeferred(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ChunkPos;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/ellpeck/naturesaura/Helper;getLoadedChunk(Lnet/minecraft/world/level/LevelAccessor;II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
            ),
            require = 0
    )
    private ChunkAccess coo$watchedChunkEvenIfNotTicking(LevelAccessor level, int x, int z) {
        if (CoOConfig.naturesauraResolveWatchedChunks && level.getChunkSource() instanceof ServerChunkCache cache) {
            return cache.getChunkNow(x, z);
        }
        return Helper.getLoadedChunk(level, x, z);
    }
}
