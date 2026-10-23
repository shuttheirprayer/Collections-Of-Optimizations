package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ParkingNotifier;
import dev.engine_room.flywheel.impl.task.ThreadGroupNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.engine_room.flywheel.impl.task.ParallelTaskExecutor$WorkerThread", remap = false)
public abstract class MixinParallelTaskExecutorWorker {

    @Redirect(method = "spinThenWait", at = @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/impl/task/ThreadGroupNotifier;awaitNotification()V"), require = 0)
    private void coo$parkWorker(ThreadGroupNotifier notifier) {
        if (CoOConfig.flywheelParkWorkers) {
            ((ParkingNotifier.Holder) notifier).coo$parking().await();
        } else {
            notifier.awaitNotification();
        }
    }
}
