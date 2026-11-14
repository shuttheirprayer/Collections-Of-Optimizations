package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import dev.engine_room.flywheel.backend.compile.PipelineCompiler;
import dev.engine_room.flywheel.backend.compile.core.CompilationHarness;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PipelineCompiler.class, remap = false)
public interface FlwPipelineCompilerAccessor {
    @Accessor("harness")
    CompilationHarness<PipelineCompiler.PipelineProgramKey> coo$harness();
}
