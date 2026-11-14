package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import com.misanthropy.collections_of_optimizations.core.FlywheelWarm;
import dev.engine_room.flywheel.backend.compile.core.CompilationHarness;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CompilationHarness.class, remap = false)
public abstract class MixinCompilationHarnessWarm {

    @Inject(method = "compile", at = @At("RETURN"))
    private void coo$record(Object key, CallbackInfoReturnable<?> cir) {
        FlywheelWarm.compiled(this, key);
    }
}
