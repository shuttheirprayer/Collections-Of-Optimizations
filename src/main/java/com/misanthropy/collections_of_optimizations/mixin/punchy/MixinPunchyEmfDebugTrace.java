package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchy.client.debug.EmfEtfFreshAnimationsDebug;

import java.util.ArrayDeque;

@Mixin(value = EmfEtfFreshAnimationsDebug.class, remap = false)
public abstract class MixinPunchyEmfDebugTrace {

    @Shadow
    @Final
    private static ThreadLocal<ArrayDeque<?>> CONTEXT_STACK;

    @Inject(method = "pushLivingEntityRender", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipTracePush(LivingEntity entity, Object renderer, Object model, CallbackInfo ci) {
        if (CoOConfig.punchySkipEmfDebugTrace) {
            ci.cancel();
        }
    }

    @Inject(method = "popLivingEntityRender", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipTracePop(CallbackInfo ci) {
        if (CoOConfig.punchySkipEmfDebugTrace && CONTEXT_STACK.get().isEmpty()) {
            ci.cancel();
        }
    }
}
