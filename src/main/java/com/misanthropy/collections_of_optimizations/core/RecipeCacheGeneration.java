package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public final class RecipeCacheGeneration {

    private static final AtomicInteger GENERATION = new AtomicInteger(1);

    private RecipeCacheGeneration() {
    }

    public static int generation() {
        return GENERATION.get();
    }

    public static void invalidate() {
        GENERATION.incrementAndGet();
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(RecipeCacheGeneration::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(RecipeCacheGeneration::onDatapackSync);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            RecipeCacheClientHook.register();
        }
    }

    private static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void unused, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
                invalidate();
            }
        });
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        invalidate();
    }
}
