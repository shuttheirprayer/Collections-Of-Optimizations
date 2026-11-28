package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.Pathfinding;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CitadelPathQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(value = Pathfinding.class, remap = false)
public abstract class MixinPathfindingBoundedQueue {

    @WrapOperation(
            method = "getExecutor",
            at = @At(value = "NEW", target = "java/util/concurrent/ThreadPoolExecutor"),
            require = 0
    )
    private static ThreadPoolExecutor coo$boundedQueue(int core, int max, long keepAlive, TimeUnit unit, BlockingQueue<Runnable> queue,
                                                       ThreadFactory factory, Operation<ThreadPoolExecutor> original) {
        if (!CoOConfig.citadelBoundedPathQueue) {
            return original.call(core, max, keepAlive, unit, queue, factory);
        }
        ThreadPoolExecutor executor = original.call(core, max, keepAlive, unit, new ArrayBlockingQueue<Runnable>(CitadelPathQueue.CAPACITY), factory);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
