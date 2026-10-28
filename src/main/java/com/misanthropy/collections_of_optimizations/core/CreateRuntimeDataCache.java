package com.misanthropy.collections_of_optimizations.core;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.create.RuntimeDataGeneratorAccessor;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CreateRuntimeDataCache {
    private static volatile Map<ResourceLocation, byte[]> cached;

    private CreateRuntimeDataCache() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLLoadCompleteEvent event) -> {
            if (!CoOConfig.createCacheRuntimeData) {
                return;
            }
            Thread thread = new Thread(CreateRuntimeDataCache::get, "CoO Create runtime data");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        });
    }

    public static Map<ResourceLocation, byte[]> get() {
        Map<ResourceLocation, byte[]> result = cached;
        if (result != null) {
            return result;
        }
        synchronized (CreateRuntimeDataCache.class) {
            if (cached != null) {
                return cached;
            }
            for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
                RuntimeDataGeneratorAccessor.coo$cuttingRecipes(itemId);
                RuntimeDataGeneratorAccessor.coo$washingRecipes(itemId);
            }
            Map<ResourceLocation, JsonObject> files = RuntimeDataGeneratorAccessor.coo$jsonFiles();
            Multimap<ResourceLocation, TagEntry> tags = RuntimeDataGeneratorAccessor.coo$tags();
            Map<ResourceLocation, byte[]> out = new HashMap<>();
            files.forEach((location, json) -> out.put(location.withSuffix(".json"), bytes(Create.GSON.toJson(json))));
            Create.LOGGER.info("Created {} recipes which will be injected into the game", files.size());
            Create.LOGGER.info("Created {} tags which will be injected into the game", tags.size());
            for (Map.Entry<ResourceLocation, Collection<TagEntry>> entry : tags.asMap().entrySet()) {
                TagFile tagFile = new TagFile(new ArrayList<>(entry.getValue()), false);
                out.put(entry.getKey().withPrefix("tags/items/").withSuffix(".json"),
                        bytes(Create.GSON.toJson(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, tagFile).result().orElseThrow())));
            }
            files.clear();
            tags.clear();
            cached = Map.copyOf(out);
            return cached;
        }
    }

    private static byte[] bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
