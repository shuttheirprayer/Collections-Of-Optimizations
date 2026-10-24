package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WarmClasses {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int MAX_CLASSES = 60000;
    private static final long RECORD_NANOS = Duration.ofMinutes(15).toNanos();
    private static final int WORLD_TICKS_BEFORE_STOP = 100;

    private static volatile boolean stopPreload;
    private static boolean started;
    private static boolean recording;
    private static long recordStart;
    private static int worldTicks;
    private static Object stream;
    private static final ConcurrentLinkedQueue<String> SEEN = new ConcurrentLinkedQueue<>();
    private static List<String> known = List.of();

    private WarmClasses() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(WarmClasses::onScreenOpening);
        MinecraftForge.EVENT_BUS.addListener(WarmClasses::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(WarmClasses::onShutdown);
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("collections_of_optimizations").resolve("warm-classes.txt");
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof TitleScreen) || !CoOConfig.vanillaWarmClasses) {
            return;
        }
        if (started) {
            save();
            return;
        }
        started = true;
        try {
            Path path = file();
            known = Files.exists(path) ? Files.readAllLines(path, StandardCharsets.UTF_8) : List.of();
        } catch (Throwable t) {
            known = List.of();
        }
        startRecording();
        if (known.isEmpty()) {
            return;
        }
        List<String> names = known;
        ClassLoader loader = Minecraft.class.getClassLoader();
        Thread thread = new Thread(() -> preload(names, loader), "CoO class warmup");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private static void preload(List<String> names, ClassLoader loader) {
        long begin = System.nanoTime();
        int loaded = 0;
        for (String name : names) {
            if (stopPreload) {
                break;
            }
            try {
                Class.forName(name, false, loader);
                loaded++;
            } catch (Throwable ignored) {
            }
        }
        LOGGER.info("Class warmup touched {} of {} classes in {} ms{}", loaded, names.size(), (System.nanoTime() - begin) / 1_000_000L,
                stopPreload ? ", stopped once the world was running" : "");
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !started) {
            return;
        }
        if (Minecraft.getInstance().level != null && !stopPreload && ++worldTicks >= WORLD_TICKS_BEFORE_STOP) {
            stopPreload = true;
        }
        if (recording && System.nanoTime() - recordStart > RECORD_NANOS) {
            stopRecording();
            save();
        }
    }

    private static void onShutdown(GameShuttingDownEvent event) {
        stopPreload = true;
        stopRecording();
        save();
    }

    private static void startRecording() {
        try {
            jdk.jfr.consumer.RecordingStream rs = new jdk.jfr.consumer.RecordingStream();
            rs.enable("jdk.ClassLoad").withoutStackTrace();
            rs.onEvent("jdk.ClassLoad", e -> {
                jdk.jfr.consumer.RecordedClass type = e.getClass("loadedClass");
                if (type != null) {
                    String name = type.getName();
                    if (name.indexOf('/') < 0 && name.indexOf('[') < 0 && !name.contains("$$Lambda")) {
                        SEEN.add(name);
                    }
                }
            });
            rs.startAsync();
            stream = rs;
            recording = true;
            recordStart = System.nanoTime();
        } catch (Throwable t) {
            LOGGER.info("Class warmup recording is unavailable on this Java runtime, the warmup list is not updated");
        }
    }

    private static void stopRecording() {
        if (!recording) {
            return;
        }
        recording = false;
        try {
            ((jdk.jfr.consumer.RecordingStream) stream).close();
        } catch (Throwable ignored) {
        }
        stream = null;
    }

    private static synchronized void save() {
        if (SEEN.isEmpty()) {
            return;
        }
        Set<String> merged = new LinkedHashSet<>(known);
        String name;
        while ((name = SEEN.poll()) != null) {
            merged.add(name);
        }
        List<String> out = new ArrayList<>(merged);
        if (out.size() > MAX_CLASSES) {
            out = out.subList(out.size() - MAX_CLASSES, out.size());
        }
        try {
            Path path = file();
            Files.createDirectories(path.getParent());
            Path temp = path.resolveSibling("warm-classes.txt.tmp");
            Files.write(temp, out, StandardCharsets.UTF_8);
            Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            known = out;
        } catch (Throwable t) {
            LOGGER.warn("Could not save the class warmup list", t);
        }
    }
}
