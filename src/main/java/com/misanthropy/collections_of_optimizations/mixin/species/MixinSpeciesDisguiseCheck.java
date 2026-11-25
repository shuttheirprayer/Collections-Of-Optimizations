package com.misanthropy.collections_of_optimizations.mixin.species;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Pseudo
@Mixin(targets = "com.ninni.species.server.disguise.DisguiseTickContext", remap = false)
public abstract class MixinSpeciesDisguiseCheck {

    @Shadow
    @Final
    private static AtomicInteger ACTIVE_TICKS;

    @Unique
    private static final ArrayDeque<?> coo$nobody = new ArrayDeque<>(1);

    @Redirect(method = "isDisguiseTicking", at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"), require = 0)
    private static Object coo$skipWhenNobodyTicks(ThreadLocal<?> sources) {
        return CoOConfig.speciesSkipIdleDisguiseCheck && ACTIVE_TICKS.get() == 0 ? coo$nobody : sources.get();
    }
}
