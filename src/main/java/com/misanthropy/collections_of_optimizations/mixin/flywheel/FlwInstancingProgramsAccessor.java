package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import dev.engine_room.flywheel.backend.compile.InstancingPrograms;
import dev.engine_room.flywheel.backend.compile.PipelineCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InstancingPrograms.class, remap = false)
public interface FlwInstancingProgramsAccessor {
    @Accessor("pipeline")
    PipelineCompiler coo$pipeline();
}
