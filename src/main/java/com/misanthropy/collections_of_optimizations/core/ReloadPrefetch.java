package com.misanthropy.collections_of_optimizations.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public final class ReloadPrefetch {

    private static final String[] SIDEBAR_FILES = {"sidebar_button_groups.json", "sidebar_buttons.json"};

    private static volatile LanguageJob language;
    private static volatile SidebarJob sidebar;

    private ReloadPrefetch() {
    }

    private record LanguageJob(ResourceManager manager, CompletableFuture<Prepared> result) {
    }

    private record Prepared(List<String> codes, boolean rightToLeft, ClientLanguage language) {
    }

    private record Sidebar(Map<ResourceLocation, List<Resource>> stacks, Map<Resource, JsonElement> parsed) {
    }

    private record SidebarJob(ResourceManager manager, CompletableFuture<Sidebar> result) {
    }

    public static void start(ResourceManager manager, Executor executor) {
        language = null;
        sidebar = null;
        if (CoOConfig.vanillaPrepareLanguage) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null && minecraft.getLanguageManager() != null) {
                String selected = minecraft.getLanguageManager().getSelected();
                language = new LanguageJob(manager, CompletableFuture.supplyAsync(() -> prepareLanguage(manager, selected), executor));
            }
        }
        if (CoOConfig.ftblibraryPrepareSidebar && ModList.get().isLoaded("ftblibrary")) {
            sidebar = new SidebarJob(manager, CompletableFuture.supplyAsync(() -> readSidebar(manager), executor));
        }
    }

    public static ClientLanguage language(ResourceManager manager, List<String> codes, boolean rightToLeft) {
        LanguageJob job = language;
        if (job == null || job.manager != manager) {
            return null;
        }
        language = null;
        try {
            Prepared prepared = job.result.getNow(null);
            return prepared != null && prepared.rightToLeft == rightToLeft && prepared.codes.equals(codes) ? prepared.language : null;
        } catch (CompletionException failure) {
            return null;
        }
    }

    private static Prepared prepareLanguage(ResourceManager manager, String selected) {
        Map<String, LanguageInfo> languages = new HashMap<>();
        manager.listPacks().forEach(pack -> {
            try {
                LanguageMetadataSection section = pack.getMetadataSection(LanguageMetadataSection.TYPE);
                if (section != null) {
                    section.languages().forEach(languages::putIfAbsent);
                }
            } catch (IOException | RuntimeException ignored) {
            }
        });
        List<String> codes = new ArrayList<>(2);
        boolean rightToLeft = false;
        codes.add("en_us");
        if (!"en_us".equals(selected)) {
            LanguageInfo info = languages.get(selected);
            if (info != null) {
                codes.add(selected);
                rightToLeft = info.bidirectional();
            }
        }
        return new Prepared(codes, rightToLeft, ClientLanguage.loadFrom(manager, codes, rightToLeft));
    }

    public static List<Resource> sidebarStack(ResourceManager manager, ResourceLocation id) {
        Sidebar data = sidebar(manager);
        return data == null ? null : data.stacks.get(id);
    }

    public static JsonElement sidebarJson(ResourceManager manager, Resource resource) {
        Sidebar data = sidebar(manager);
        return data == null ? null : data.parsed.get(resource);
    }

    private static Sidebar sidebar(ResourceManager manager) {
        SidebarJob job = sidebar;
        if (job == null || job.manager != manager) {
            return null;
        }
        try {
            return job.result.getNow(null);
        } catch (CompletionException failure) {
            return null;
        }
    }

    private static Sidebar readSidebar(ResourceManager manager) {
        Map<ResourceLocation, List<Resource>> stacks = new HashMap<>();
        Map<Resource, JsonElement> parsed = new IdentityHashMap<>();
        for (String namespace : manager.getNamespaces()) {
            for (String file : SIDEBAR_FILES) {
                ResourceLocation id = new ResourceLocation(namespace, file);
                try {
                    List<Resource> resources = manager.getResourceStack(id);
                    if (resources.isEmpty()) {
                        continue;
                    }
                    Map<Resource, JsonElement> fresh = new IdentityHashMap<>();
                    for (Resource resource : resources) {
                        try (BufferedReader reader = resource.openAsReader()) {
                            fresh.put(resource, JsonParser.parseReader(reader));
                        }
                    }
                    stacks.put(id, resources);
                    parsed.putAll(fresh);
                } catch (Exception ignored) {
                }
            }
        }
        return new Sidebar(stacks, parsed);
    }
}
