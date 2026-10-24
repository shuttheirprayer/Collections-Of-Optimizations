package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FrameWorkBudget;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevelLightPollBudget {

    @Shadow
    @Final
    private Deque<Runnable> lightUpdateQueue;

    @Inject(method = "pollLightUpdates", at = @At("HEAD"), cancellable = true)
    private void coo$budgetedLightPoll(CallbackInfo ci) {
        if (!CoOConfig.vanillaBudgetLightPolls) {
            return;
        }
        ci.cancel();
        Deque<Runnable> queue = this.lightUpdateQueue;
        int size = queue.size();
        if (size == 0) {
            return;
        }
        int limit = size < 1000 ? Math.max(10, size / 10) : size;
        long deadline = System.nanoTime() + FrameWorkBudget.budgetNanos(size);
        for (int index = 0; index < limit; index++) {
            Runnable update = queue.poll();
            if (update == null) {
                return;
            }
            update.run();
            if (System.nanoTime() - deadline >= 0L) {
                return;
            }
        }
    }
}
