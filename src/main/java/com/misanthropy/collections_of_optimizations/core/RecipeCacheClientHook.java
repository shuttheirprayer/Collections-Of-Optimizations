package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;

public final class RecipeCacheClientHook {

    private RecipeCacheClientHook() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(RecipeCacheClientHook::onRecipesUpdated);
    }

    private static void onRecipesUpdated(RecipesUpdatedEvent event) {
        RecipeCacheGeneration.invalidate();
    }
}
