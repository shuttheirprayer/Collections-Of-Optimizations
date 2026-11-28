package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ParallelMips {

    private static final int CHUNK = 32;
    private static final int MIN_SPRITES = 64;

    private ParallelMips() {
    }

    public static CompletableFuture<Void> generate(Map<ResourceLocation, TextureAtlasSprite> regions, int mipLevel, Executor executor) {
        TextureAtlasSprite[] sprites = regions.values().toArray(new TextureAtlasSprite[0]);
        int chunk = sprites.length < MIN_SPRITES ? Math.max(sprites.length, 1) : CHUNK;
        int count = (sprites.length + chunk - 1) / chunk;
        CompletableFuture<?>[] tasks = new CompletableFuture<?>[count];
        for (int i = 0; i < count; i++) {
            int from = i * chunk;
            int to = Math.min(from + chunk, sprites.length);
            tasks[i] = CompletableFuture.runAsync(() -> {
                for (int k = from; k < to; k++) {
                    sprites[k].contents().increaseMipLevel(mipLevel);
                }
            }, executor);
        }
        return CompletableFuture.allOf(tasks);
    }
}
