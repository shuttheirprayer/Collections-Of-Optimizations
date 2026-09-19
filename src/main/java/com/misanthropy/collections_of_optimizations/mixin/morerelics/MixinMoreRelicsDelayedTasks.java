package com.misanthropy.collections_of_optimizations.mixin.morerelics;

import com.blorb.morerelics.helper.DelayedTask;
import com.blorb.morerelics.helper.DelayedTaskManager;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.List;

@Mixin(value = DelayedTaskManager.class, remap = false)
public abstract class MixinMoreRelicsDelayedTasks {

    @Unique
    private static WeakReference<MinecraftServer> coo$lastServer = new WeakReference<>(null);

    @Inject(method = "onServerEvent", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$safeDelayedTaskPass(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.morerelicsSafeDelayedTasks) {
            return;
        }
        ci.cancel();
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        List<DelayedTask> tasks = DelayedTaskManager.tasks;
        if (tasks == null) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (coo$lastServer.get() != server) {
            coo$lastServer = new WeakReference<>(server);
            tasks.clear();
            return;
        }
        if (tasks.isEmpty()) {
            return;
        }
        Object[] snapshot = tasks.toArray();
        for (Object entry : snapshot) {
            DelayedTask task = (DelayedTask) entry;
            if (task.tickDelay > 0) {
                task.tickDelay--;
                continue;
            }
            tasks.remove(task);
            Runnable runnable = task.runnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
