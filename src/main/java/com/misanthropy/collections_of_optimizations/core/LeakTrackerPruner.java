package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class LeakTrackerPruner {
    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final long PERIOD_SECONDS = 5L;

    private LeakTrackerPruner() {
    }

    public static void register() {
        Method prune;
        try {
            prune = Class.forName("dev.uncandango.alltheleaks.mixin.Trackable").getMethod("clearNullReferences");
        } catch (Throwable failure) {
            LOGGER.warn("All The Leaks tracker not found, dead reference pruning is off", failure);
            return;
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "CoO leak tracker pruner");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });
        executor.scheduleWithFixedDelay(() -> {
            if (!CoOConfig.alltheleaksPruneDeadReferences) {
                return;
            }
            try {
                prune.invoke(null);
            } catch (Throwable failure) {
                LOGGER.warn("Pruning All The Leaks tracker failed, stopping", failure);
                executor.shutdown();
            }
        }, PERIOD_SECONDS, PERIOD_SECONDS, TimeUnit.SECONDS);
    }
}
