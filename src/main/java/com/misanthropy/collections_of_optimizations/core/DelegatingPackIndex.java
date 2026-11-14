package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DelegatingPackIndex {

    private static final long RETRY_NANOS = 250_000_000L;
    private static final java.util.Set<String> LOGGED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private final List<PackResources> packs;
    private final List<PackResources> unindexed;
    private final List<PackResources>[] tails;
    private final Long2IntOpenHashMap first;
    private final int firstUnindexed;
    private final long builtAt;

    private DelegatingPackIndex(List<PackResources> packs, List<PackResources> unindexed, List<PackResources>[] tails,
                                Long2IntOpenHashMap first, int firstUnindexed) {
        this.packs = packs;
        this.unindexed = unindexed;
        this.tails = tails;
        this.first = first;
        this.firstUnindexed = firstUnindexed;
        this.builtAt = System.nanoTime();
    }

    public boolean stale(List<PackResources> current) {
        return current != packs || first == null && System.nanoTime() - builtAt > RETRY_NANOS;
    }

    public List<PackResources> candidates(ResourceLocation location) {
        if (first == null) {
            return packs;
        }
        long hash = PackPathHash.of(location.getNamespace(), location.getPath());
        if (hash == 0L) {
            return packs;
        }
        int at = first.get(hash);
        if (at < 0) {
            return unindexed;
        }
        return at < firstUnindexed ? tails[at] : packs;
    }

    @SuppressWarnings("unchecked")
    public static DelegatingPackIndex build(String top, String namespace, List<PackResources> packs) {
        int size = packs.size();
        ModFileIndex[] indexes = new ModFileIndex[size];
        List<PackResources> unindexed = new ArrayList<>();
        int firstUnindexed = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            PackResources pack = packs.get(i);
            Path source = jarSource(pack);
            ModFileIndex index = source == null ? null : ModFileIndex.cached(source);
            if (source != null && index == null) {
                return new DelegatingPackIndex(packs, packs, null, null, 0);
            }
            if (index == null || !index.usable()) {
                unindexed.add(pack);
                firstUnindexed = Math.min(firstUnindexed, i);
            } else {
                indexes[i] = index;
            }
        }
        Long2IntOpenHashMap first = new Long2IntOpenHashMap();
        first.defaultReturnValue(-1);
        for (int i = 0; i < size; i++) {
            if (indexes[i] == null) {
                continue;
            }
            int position = i;
            indexes[i].hashes(top, namespace, hash -> first.putIfAbsent(hash, position));
        }
        first.trim();
        if (LOGGED.add(top + ":" + namespace)) {
            org.slf4j.LoggerFactory.getLogger("collections_of_optimizations").info("Indexed {} {} files of namespace {} across {} mod packs ({} not indexed)",
                    first.size(), top, namespace, size, unindexed.size());
        }
        List<PackResources>[] tails = new List[size];
        for (int i = 0; i < size; i++) {
            tails[i] = packs.subList(i, size);
        }
        return new DelegatingPackIndex(packs, unindexed.isEmpty() ? Collections.emptyList() : List.copyOf(unindexed), tails, first, firstUnindexed);
    }

    private static Path jarSource(PackResources pack) {
        if (!(pack instanceof PathPackResources forge)) {
            return null;
        }
        Path source = forge.getSource();
        if (source == null || source.getFileSystem() == FileSystems.getDefault() && !source.toString().endsWith(".jar")) {
            return null;
        }
        return source;
    }
}
