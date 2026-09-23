package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class L2LibraryTrackedEffects {

    private static final int MAX_ENTRIES = 512;

    private static final AtomicInteger GENERATION = new AtomicInteger(1);

    private static final ThreadLocal<L2LibraryTrackedEffects.Local> LOCAL =
            ThreadLocal.withInitial(L2LibraryTrackedEffects.Local::new);

    private L2LibraryTrackedEffects() {
    }

    public static Boolean get(MobEffect effect, int trackedSize) {
        if (effect == null) {
            return null;
        }
        L2LibraryTrackedEffects.Local local = LOCAL.get();
        if (!local.valid(trackedSize)) {
            return null;
        }
        return local.map.get(effect);
    }

    public static void put(MobEffect effect, int trackedSize, boolean value) {
        if (effect == null) {
            return;
        }
        L2LibraryTrackedEffects.Local local = LOCAL.get();
        local.rebase(trackedSize);
        if (local.map.size() >= MAX_ENTRIES) {
            local.map.clear();
        }
        local.map.put(effect, value);
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(L2LibraryTrackedEffects::onTagsUpdated);
    }

    private static void onTagsUpdated(TagsUpdatedEvent event) {
        GENERATION.incrementAndGet();
    }

    private static final class Local {

        private final Map<MobEffect, Boolean> map = new IdentityHashMap<>();
        private int generation;
        private int trackedSize = -1;

        private boolean valid(int size) {
            return this.generation == GENERATION.get() && this.trackedSize == size;
        }

        private void rebase(int size) {
            if (!valid(size)) {
                this.map.clear();
                this.generation = GENERATION.get();
                this.trackedSize = size;
            }
        }
    }
}
