package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelChunk.class)
public interface LevelChunkGameEventAccessor {

    @Accessor("gameEventListenerRegistrySections")
    Int2ObjectMap<GameEventListenerRegistry> coo$gameEventRegistries();
}
