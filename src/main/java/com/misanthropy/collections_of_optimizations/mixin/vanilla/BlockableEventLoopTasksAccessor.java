package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockableEventLoop.class)
public interface BlockableEventLoopTasksAccessor {

    @Invoker("runAllTasks")
    void coo$runAllTasks();
}
