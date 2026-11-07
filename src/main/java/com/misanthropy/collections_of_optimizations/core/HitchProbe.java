package com.misanthropy.collections_of_optimizations.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

public final class HitchProbe {

    public static final boolean ENABLED = Boolean.getBoolean("coo.hitchProbe");

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final long THRESHOLD = 60_000_000L;
    private static volatile long tickStart;
    private static Thread render;

    private HitchProbe() {
    }

    public static void tickStart() {
        if (render == null) {
            render = Thread.currentThread();
            Thread watcher = new Thread(HitchProbe::watch, "CoO hitch probe");
            watcher.setDaemon(true);
            watcher.setPriority(Thread.MAX_PRIORITY);
            watcher.start();
        }
        tickStart = System.nanoTime();
    }

    public static void tickEnd() {
        tickStart = 0L;
    }

    private static void watch() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long id = render.getId();
        while (true) {
            try {
                Thread.sleep(5L);
            } catch (InterruptedException e) {
                return;
            }
            long start = tickStart;
            if (start == 0L || System.nanoTime() - start < THRESHOLD) {
                continue;
            }
            long cpuStart = bean.getThreadCpuTime(id);
            Map<String, Integer> stacks = new HashMap<>();
            int samples = 0;
            long slowest = 0L;
            while (tickStart == start) {
                long before = System.nanoTime();
                StackTraceElement[] trace = render.getStackTrace();
                long took = System.nanoTime() - before;
                slowest = Math.max(slowest, took);
                StringBuilder key = new StringBuilder(render.getState().name()).append(' ');
                for (int i = 0; i < Math.min(trace.length, 14); i++) {
                    key.append(trace[i].getClassName().substring(trace[i].getClassName().lastIndexOf('.') + 1)).append('.')
                            .append(trace[i].getMethodName()).append(':').append(trace[i].getLineNumber()).append(" < ");
                }
                stacks.merge(key.toString(), 1, Integer::sum);
                samples++;
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException e) {
                    return;
                }
            }
            long total = (System.nanoTime() - start) / 1_000_000L;
            long cpu = (bean.getThreadCpuTime(id) - cpuStart) / 1_000_000L;
            LOGGER.info("[hitch] frame {} ms, render thread cpu {} ms over the sampled part, {} samples, slowest stack grab {} ms", total, cpu, samples, slowest / 1_000_000L);
            stacks.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(12)
                    .forEach(e -> LOGGER.info("[hitch]   {} x {}", e.getValue(), e.getKey()));
        }
    }
}
