package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import com.misanthropy.collections_of_optimizations.core.FlywheelWarm;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleInstanceType.class, remap = false)
public abstract class MixinSimpleInstanceTypeWarm {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void coo$register(CallbackInfo ci) {
        FlywheelWarm.registerType((InstanceType<?>) this);
    }
}
