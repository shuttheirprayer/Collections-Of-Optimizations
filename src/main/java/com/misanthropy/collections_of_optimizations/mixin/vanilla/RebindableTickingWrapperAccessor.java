package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$RebindableTickingBlockEntityWrapper")
public interface RebindableTickingWrapperAccessor {

    @Accessor("ticker")
    TickingBlockEntity coo$ticker();
}
