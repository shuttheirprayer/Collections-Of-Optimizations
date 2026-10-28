package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.FrameWorkBudget;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockableEventLoop.class)
public abstract class MixinBlockableEventLoopFrameBudget {

    @Unique
    private long coo$taskDeadline;

    @Unique
    private boolean coo$overran;

    @Inject(method = "runAllTasks", at = @At("HEAD"))
    private void coo$startTaskBudget(CallbackInfo ci) {
        this.coo$overran = false;
        this.coo$taskDeadline = FrameWorkBudget.taskDeadline((BlockableEventLoop<?>) (Object) this);
    }

    @Redirect(
            method = "runAllTasks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/BlockableEventLoop;pollTask()Z")
    )
    private boolean coo$budgetedPoll(BlockableEventLoop<?> loop) {
        long deadline = this.coo$taskDeadline;
        if (deadline != 0L && System.nanoTime() - deadline >= 0L) {
            this.coo$taskDeadline = 0L;
            this.coo$overran = true;
            return false;
        }
        return loop.pollTask();
    }

    @Inject(method = "runAllTasks", at = @At("TAIL"))
    private void coo$finishTaskBudget(CallbackInfo ci) {
        if (FrameWorkBudget.isClientLoop((BlockableEventLoop<?>) (Object) this)) {
            FrameWorkBudget.tasksFinished(this.coo$overran);
        }
    }
}
