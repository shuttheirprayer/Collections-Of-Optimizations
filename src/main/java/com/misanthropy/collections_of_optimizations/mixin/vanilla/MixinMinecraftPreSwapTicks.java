package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.PreSwapTicks;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftPreSwapTicks {

    @Shadow
    @Final
    private Timer timer;

    @Shadow
    public abstract void tick();

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateDisplay()V"))
    private void coo$tickBeforeSwap(boolean renderLevel, CallbackInfo ci) {
        if (!renderLevel || !PreSwapTicks.active()) {
            return;
        }
        int ticks = this.timer.advanceTime(Util.getMillis());
        ((BlockableEventLoopTasksAccessor) this).coo$runAllTasks();
        int count = Math.min(10, ticks);
        for (int i = 0; i < count; i++) {
            PreSwapTicks.beforeTick(i == count - 1);
            this.tick();
            PreSwapTicks.afterTick();
        }
        PreSwapTicks.ticked = true;
    }

    @WrapOperation(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Timer;advanceTime(J)I"))
    private int coo$skipPreSwapTicked(Timer timer, long millis, Operation<Integer> original) {
        if (PreSwapTicks.ticked) {
            PreSwapTicks.ticked = false;
            return 0;
        }
        return original.call(timer, millis);
    }
}
