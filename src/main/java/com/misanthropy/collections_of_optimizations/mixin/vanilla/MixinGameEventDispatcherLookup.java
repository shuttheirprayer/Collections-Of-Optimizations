package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameEventDispatcher.class)
public abstract class MixinGameEventDispatcherLookup {

    @WrapOperation(
            method = "post",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getListenerRegistry(I)Lnet/minecraft/world/level/gameevent/GameEventListenerRegistry;"
            ),
            require = 0
    )
    private GameEventListenerRegistry coo$existingRegistryOnly(ChunkAccess chunk, int sectionY, Operation<GameEventListenerRegistry> original) {
        if (CoOConfig.vanillaLeanGameEventLookup && chunk.getClass() == LevelChunk.class) {
            GameEventListenerRegistry registry = ((LevelChunkGameEventAccessor) chunk).coo$gameEventRegistries().get(sectionY);
            return registry != null ? registry : GameEventListenerRegistry.NOOP;
        }
        return original.call(chunk, sectionY);
    }
}
