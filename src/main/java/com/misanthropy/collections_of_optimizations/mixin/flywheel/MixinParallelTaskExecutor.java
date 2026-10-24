package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ParkingNotifier;
import dev.engine_room.flywheel.impl.task.ParallelTaskExecutor;
import dev.engine_room.flywheel.impl.task.ThreadGroupNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ParallelTaskExecutor.class, remap = false)
public abstract class MixinParallelTaskExecutor {

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/impl/task/ThreadGroupNotifier;postNotification()V"), require = 0)
    private void coo$unparkWorkers(ThreadGroupNotifier notifier) {
        if (CoOConfig.flywheelParkWorkers) {
            ((ParkingNotifier.Holder) notifier).coo$parking().post();
        } else {
            notifier.postNotification();
        }
    }

    @Redirect(method = "stopWorkers", at = @At(value = "INVOKE", target = "Ljava/lang/Object;notifyAll()V"), require = 0)
    private void coo$unparkOnStop(Object notifier) {
        notifier.notifyAll();
        if (notifier instanceof ParkingNotifier.Holder holder) {
            holder.coo$parking().postAll();
        }
    }
}
