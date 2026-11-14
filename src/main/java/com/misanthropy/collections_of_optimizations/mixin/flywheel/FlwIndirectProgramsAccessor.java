package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.backend.compile.IndirectPrograms;
import dev.engine_room.flywheel.backend.compile.PipelineCompiler;
import dev.engine_room.flywheel.backend.compile.core.CompilationHarness;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = IndirectPrograms.class, remap = false)
public interface FlwIndirectProgramsAccessor {
    @Accessor("pipeline")
    PipelineCompiler coo$pipeline();

    @Accessor("culling")
    CompilationHarness<InstanceType<?>> coo$culling();
}
