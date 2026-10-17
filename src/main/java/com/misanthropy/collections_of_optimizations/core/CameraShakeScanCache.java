package com.misanthropy.collections_of_optimizations.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class CameraShakeScanCache {

    private static final Map<Class<?>, List<Entity>> CACHE = new Reference2ObjectOpenHashMap<>(16);

    private static final Map<Class<?>, AABB> BOXES = new Reference2ObjectOpenHashMap<>(16);

    private static final Map<String, List<? extends Entity>> KEYED = new HashMap<>(4);

    private static WeakReference<Level> scanned = new WeakReference<>(null);

    private static int stamp = Integer.MIN_VALUE;

    private CameraShakeScanCache() {
    }

    public static void register() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(CameraShakeScanCache::onLoggingOut);
        }
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        stamp = Integer.MIN_VALUE;
        scanned = new WeakReference<>(null);
        CACHE.clear();
        BOXES.clear();
        KEYED.clear();
    }

    private static void refresh(Level level) {
        int now = ClientTickStamp.current();
        if (stamp != now || scanned.get() != level) {
            stamp = now;
            scanned = new WeakReference<>(level);
            CACHE.clear();
            BOXES.clear();
            KEYED.clear();
        }
    }

    public static List<Entity> get(Level level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        if (type == null || !Minecraft.getInstance().isSameThread()) {
            return original.call(level, type, box);
        }
        refresh(level);
        List<Entity> cached = CACHE.get(type);
        if (cached == null) {
            cached = original.call(level, type, box);
            CACHE.put(type, cached);
            BOXES.put(type, box);
            return cached;
        }
        AABB first = BOXES.get(type);
        if (Math.abs(first.getXsize() - box.getXsize()) > 0.01 || Math.abs(first.getYsize() - box.getYsize()) > 0.01
                || Math.abs(first.getZsize() - box.getZsize()) > 0.01) {
            return original.call(level, type, box);
        }
        return cached;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> get(String key, Level level, Class<T> type, AABB box,
                                                 Predicate<? super T> filter, Operation<List<T>> original) {
        if (!Minecraft.getInstance().isSameThread()) {
            return original.call(level, type, box, filter);
        }
        refresh(level);
        List<T> cached = (List<T>) KEYED.get(key);
        if (cached == null) {
            cached = original.call(level, type, box, filter);
            KEYED.put(key, cached);
        }
        return cached;
    }
}
