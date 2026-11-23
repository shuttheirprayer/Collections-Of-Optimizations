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
    private static final int MAX_CLASSES = 40000;
    private static final String THREAD_NAME = "CoO class warmup";
    private static final long RECORD_NANOS = Duration.ofMinutes(15).toNanos();
    private static final int WORLD_TICKS_BEFORE_STOP = 200;

    private static volatile boolean stopPreload;
    private static boolean started;
    private static boolean recording;
    private static long recordStart;
    private static int worldTicks;
    private static java.lang.invoke.MethodHandle loaded;
    private static ClassLoader[] loaders = new ClassLoader[0];
    private static int[] read = new int[0];
    private static int[] inWorld;
    private static int[] afterPreload;
    private static final ConcurrentLinkedQueue<String> SEEN = new ConcurrentLinkedQueue<>();
    private static final Set<String> HOT = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static final Set<String> GONE = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static List<String> known = List.of();
    private static Set<String> knownHot = Set.of();

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
        if (event.getNewScreen() == null || !CoOConfig.vanillaWarmClasses) {
            return;
        }
        if (started) {
            if (event.getNewScreen() instanceof TitleScreen) {
                save();
            }
            return;
        }
        started = true;
        try {
            Path path = file();
            List<String> lines = Files.exists(path) ? Files.readAllLines(path, StandardCharsets.UTF_8) : List.of();
            List<String> names = new ArrayList<>(lines.size());
            Set<String> hot = new java.util.HashSet<>();
            for (String line : lines) {
                String name = line.trim();
                boolean isHot = name.startsWith("!");
                if (isHot) {
                    name = name.substring(1);
                }
                if (!loadable(name)) {
                    continue;
                }
                names.add(name);
                if (isHot) {
                    hot.add(name);
                }
            }
            known = names;
            knownHot = hot;
        } catch (Throwable t) {
            known = List.of();
            knownHot = Set.of();
        }
        startRecording();
        if (known.isEmpty()) {
            return;
        }
        List<String> names = hotFirst(known, knownHot);
        ClassLoader loader = Minecraft.class.getClassLoader();
        Thread thread = new Thread(() -> preload(names, loader), THREAD_NAME);
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
            } catch (ClassNotFoundException | NoClassDefFoundError gone) {
                GONE.add(name);
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
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && worldTicks < WORLD_TICKS_BEFORE_STOP + 20) {
            worldTicks++;
            if (worldTicks == WORLD_TICKS_BEFORE_STOP) {
                stopPreload = true;
            }
            if (worldTicks == WORLD_TICKS_BEFORE_STOP + 20 && recording) {
                afterPreload = sizes();
            }
        }
        if (recording && inWorld == null && (minecraft.level != null || minecraft.getSingleplayerServer() != null || minecraft.getConnection() != null)) {
            inWorld = sizes();
        }
        if (recording && System.nanoTime() - recordStart > RECORD_NANOS) {
            save();
            recording = false;
        }
    }

    private static void onShutdown(GameShuttingDownEvent event) {
        stopPreload = true;
        save();
        recording = false;
    }

    private static void startRecording() {
        try {
            java.lang.reflect.Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
            java.lang.reflect.Field lookupField = java.lang.invoke.MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            java.lang.invoke.MethodHandles.Lookup lookup = (java.lang.invoke.MethodHandles.Lookup) unsafe.getObject(
                    unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            try {
                loaded = lookup.findGetter(ClassLoader.class, "classes", ArrayList.class);
            } catch (NoSuchFieldException e) {
                loaded = lookup.findGetter(ClassLoader.class, "classes", java.util.Vector.class);
            }
            Set<ClassLoader> found = new LinkedHashSet<>();
            found.add(Minecraft.class.getClassLoader());
            found.add(WarmClasses.class.getClassLoader());
            found.add(MinecraftForge.class.getClassLoader());
            found.add(FMLPaths.class.getClassLoader());
            found.add(LogManager.class.getClassLoader());
            found.add(com.google.common.collect.ImmutableList.class.getClassLoader());
            found.add(ClassLoader.getSystemClassLoader());
            found.add(ClassLoader.getPlatformClassLoader());
            found.remove(null);
            loaders = found.toArray(new ClassLoader[0]);
            read = sizes();
            recording = true;
            recordStart = System.nanoTime();
        } catch (Throwable t) {
            loaders = new ClassLoader[0];
            LOGGER.info("Class warmup recording is unavailable on this Java runtime, the warmup list is not updated");
        }
    }

    private static int[] sizes() {
        int[] out = new int[loaders.length];
        for (int i = 0; i < out.length; i++) {
            try {
                List<?> list = (List<?>) loaded.invoke(loaders[i]);
                synchronized (list) {
                    out[i] = list.size();
                }
            } catch (Throwable t) {
                out[i] = Integer.MAX_VALUE;
            }
        }
        return out;
    }

    private static void collect() {
        if (!recording) {
            return;
        }
        Set<String> preloaded = new java.util.HashSet<>(known);
        for (int i = 0; i < loaders.length; i++) {
            Object[] fresh;
            try {
                List<?> list = (List<?>) loaded.invoke(loaders[i]);
                synchronized (list) {
                    if (read[i] >= list.size()) {
                        continue;
                    }
                    fresh = list.subList(read[i], list.size()).toArray();
                }
            } catch (Throwable t) {
                continue;
            }
            int newFrom = inWorld == null ? Integer.MAX_VALUE : inWorld[i];
            int anyFrom = afterPreload == null ? Integer.MAX_VALUE : afterPreload[i];
            for (int k = 0; k < fresh.length; k++) {
                String name = ((Class<?>) fresh[k]).getName();
                if (!loadable(name)) {
                    continue;
                }
                SEEN.add(name);
                int index = read[i] + k;
                if (index >= anyFrom || index >= newFrom && !preloaded.contains(name)) {
                    HOT.add(name);
                }
            }
            read[i] += fresh.length;
        }
    }

    private static synchronized void save() {
        collect();
        if (SEEN.isEmpty()) {
            return;
        }
        Set<String> merged = new LinkedHashSet<>(known);
        merged.removeAll(GONE);
        String name;
        while ((name = SEEN.poll()) != null) {
            merged.add(name);
        }
        Set<String> hot = new java.util.HashSet<>(knownHot);
        hot.addAll(HOT);
        hot.removeAll(GONE);
        List<String> ordered = hotFirst(new ArrayList<>(merged), hot);
        if (ordered.size() > MAX_CLASSES) {
            ordered = new ArrayList<>(ordered.subList(0, MAX_CLASSES));
        }
        List<String> out = new ArrayList<>(ordered.size());
        for (String n : ordered) {
            out.add(hot.contains(n) ? "!" + n : n);
        }
        try {
            Path path = file();
            Files.createDirectories(path.getParent());
            Path temp = path.resolveSibling("warm-classes.txt.tmp");
            Files.write(temp, out, StandardCharsets.UTF_8);
            Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            known = ordered;
            knownHot = hot;
        } catch (Throwable t) {
            LOGGER.warn("Could not save the class warmup list", t);
        }
    }

    private static boolean loadable(String name) {
        return !name.isEmpty() && name.indexOf('/') < 0 && name.indexOf('[') < 0 && name.indexOf('+') < 0
                && !name.contains("$$Lambda") && !name.startsWith("java.lang.invoke.LambdaForm") && !name.startsWith("jdk.internal.reflect.Generated")
                && !name.startsWith("jdk.proxy") && !name.startsWith("com.sun.proxy");
    }

    private static List<String> hotFirst(List<String> names, Set<String> hot) {
        if (hot.isEmpty()) {
            return names;
        }
        List<String> out = new ArrayList<>(names.size());
        for (String n : names) {
            if (hot.contains(n)) {
                out.add(n);
            }
        }
        for (String n : names) {
            if (!hot.contains(n)) {
                out.add(n);
            }
        }
        return out;
    }
}
