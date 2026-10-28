package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class MixinLevelBlockTickChunkMemo {
    @Unique
    private long coo$tickChunk = Long.MIN_VALUE;

    @Unique
    private boolean coo$tickChunkResult;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"))
    private void coo$resetTickChunkMemo(CallbackInfo ci) {
        this.coo$tickChunk = Long.MIN_VALUE;
    }

    @WrapOperation(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;shouldTickBlocksAt(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean coo$memoTickChunk(Level level, BlockPos pos, Operation<Boolean> original) {
        if (!CoOConfig.vanillaMemoBlockTickChunk) {
            return original.call(level, pos);
        }
        long key = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        if (key == this.coo$tickChunk) {
            return this.coo$tickChunkResult;
        }
        boolean result = original.call(level, pos);
        this.coo$tickChunk = key;
        this.coo$tickChunkResult = result;
        return result;
    }
}
