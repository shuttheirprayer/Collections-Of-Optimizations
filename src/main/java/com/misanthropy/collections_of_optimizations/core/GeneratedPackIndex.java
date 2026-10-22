package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GeneratedPackIndex {

    private final Map<ResourceLocation, ?> source;
    private final Map<String, Entry[]> byNamespace;

    private record Entry(String path, ResourceLocation id, IoSupplier<InputStream> data) {
    }

    private GeneratedPackIndex(Map<ResourceLocation, ?> source, Map<String, Entry[]> byNamespace) {
        this.source = source;
        this.byNamespace = byNamespace;
    }

    public boolean covers(Map<ResourceLocation, ?> generated) {
        return source == generated;
    }

    @SuppressWarnings("unchecked")
    public static GeneratedPackIndex build(Map<ResourceLocation, ?> generated) {
        Map<String, List<Entry>> lists = new HashMap<>();
        for (Map.Entry<ResourceLocation, ?> entry : generated.entrySet()) {
            ResourceLocation id = entry.getKey();
            lists.computeIfAbsent(id.getNamespace(), k -> new ArrayList<>())
                    .add(new Entry(id.getPath(), id, (IoSupplier<InputStream>) entry.getValue()));
        }
        Map<String, Entry[]> byNamespace = new HashMap<>(lists.size() * 2);
        lists.forEach((namespace, list) -> {
            Entry[] entries = list.toArray(new Entry[0]);
            Arrays.sort(entries, Comparator.comparing(Entry::path));
            byNamespace.put(namespace, entries);
        });
        return new GeneratedPackIndex(generated, byNamespace);
    }

    public void list(String namespace, String prefix, PackResources.ResourceOutput output) {
        Entry[] entries = byNamespace.get(namespace);
        if (entries == null) {
            return;
        }
        int low = 0;
        int high = entries.length;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (entries[middle].path.compareTo(prefix) < 0) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        for (int i = low; i < entries.length && entries[i].path.startsWith(prefix); i++) {
            output.accept(entries[i].id, entries[i].data);
        }
    }
}
