package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.etf.EtfManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTextureVariator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public final class EtfWarm {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int MAX_LINES = 4000;
    private static final long BUDGET_NANOS = 2_000_000L;

    private static final Set<String> KNOWN = new LinkedHashSet<>();
    private static final ArrayDeque<String> PENDING = new ArrayDeque<>();
    private static boolean loaded;
    private static boolean dirty;
    private static boolean reported;
    private static int warmed;
    private static long warmNanos;

    private EtfWarm() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(EtfWarm::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(EtfWarm::onScreenOpening);
        MinecraftForge.EVENT_BUS.addListener(EtfWarm::onShutdown);
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("collections_of_optimizations").resolve("warm-etf.txt");
    }

    private static HashMap<ResourceLocation, ETFTextureVariator> variators() {
        return ((EtfManagerAccessor) (Object) ETFManager.getInstance()).coo$variators();
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !CoOConfig.etfWarmTextures || Minecraft.getInstance().getOverlay() != null) {
            return;
        }
        if (!loaded) {
            load();
        }
        if (PENDING.isEmpty()) {
            return;
        }
        long begin = System.nanoTime();
        HashMap<ResourceLocation, ETFTextureVariator> map;
        try {
            map = variators();
        } catch (Throwable t) {
            PENDING.clear();
            return;
        }
        while (!PENDING.isEmpty() && System.nanoTime() - begin < BUDGET_NANOS) {
            String line = PENDING.poll();
            ResourceLocation id = ResourceLocation.tryParse(line);
            if (id == null || "etf_skin".equals(id.getNamespace()) || map.containsKey(id)) {
                continue;
            }
            try {
                map.put(id, ETFTextureVariator.of(id));
                warmed++;
            } catch (Throwable t) {
                KNOWN.remove(line);
                dirty = true;
            }
        }
        warmNanos += System.nanoTime() - begin;
        if (PENDING.isEmpty() && !reported) {
            reported = true;
            LOGGER.info("Entity Texture Features warmup prepared {} textures in {} ms", warmed, warmNanos / 1_000_000L);
        }
    }

    private static void collect() {
        try {
            for (ResourceLocation id : variators().keySet()) {
                if (KNOWN.size() >= MAX_LINES) {
                    break;
                }
                if (!"etf_skin".equals(id.getNamespace()) && KNOWN.add(id.toString())) {
                    dirty = true;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) {
            collect();
            save();
        }
    }

    private static void onShutdown(GameShuttingDownEvent event) {
        collect();
        save();
    }

    private static void load() {
        loaded = true;
        try {
            Path path = file();
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && KNOWN.add(trimmed)) {
                        PENDING.add(trimmed);
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Entity Texture Features warmup list could not be read", t);
        }
    }

    private static void save() {
        if (!loaded) {
            load();
        }
        if (!dirty || !CoOConfig.etfWarmTextures) {
            return;
        }
        try {
            Path path = file();
            Files.createDirectories(path.getParent());
            Path temp = path.resolveSibling("warm-etf.txt.tmp");
            Files.write(temp, new ArrayList<>(KNOWN), StandardCharsets.UTF_8);
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            dirty = false;
        } catch (Throwable t) {
            LOGGER.warn("Entity Texture Features warmup list could not be written", t);
        }
    }
}
