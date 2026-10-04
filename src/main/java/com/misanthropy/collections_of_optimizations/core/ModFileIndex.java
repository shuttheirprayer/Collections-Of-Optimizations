    package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ModFileIndex {

    private static final ModFileIndex BROKEN = new ModFileIndex(null);

    private final LongOpenHashSet hashes;

    private ModFileIndex(LongOpenHashSet hashes) {
        this.hashes = hashes;
    }

    public boolean usable() {
        return hashes != null;
    }

    public boolean mightContain(Path path) {
        return hashes.contains(hash(path.toString()));
    }

    public static ModFileIndex build(Function<String[], Path> resolver) {
        LongOpenHashSet hashes = new LongOpenHashSet();
        for (String top : new String[]{"assets", "data"}) {
            Path root = resolver.apply(new String[]{top});
            if (!Files.isDirectory(root)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path file : (Iterable<Path>) walk::iterator) {
                    if (!Files.isRegularFile(file)) {
                        continue;
                    }
                    if (hashes.isEmpty() && !sameForm(resolver, top, root, file)) {
                        return BROKEN;
                    }
                    hashes.add(hash(file.toString()));
                }
            } catch (IOException | RuntimeException e) {
                return BROKEN;
            }
        }
        return new ModFileIndex(hashes);
    }

    public static boolean indexed(String first) {
        return "assets".equals(first) || "data".equals(first);
    }

    private static boolean sameForm(Function<String[], Path> resolver, String top, Path root, Path file) {
        Path relative = root.relativize(file);
        String[] segments = new String[relative.getNameCount() + 1];
        segments[0] = top;
        for (int i = 0; i < relative.getNameCount(); i++) {
            segments[i + 1] = relative.getName(i).toString();
        }
        return resolver.apply(segments).toString().equals(file.toString());
    }

    private static long hash(String s) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            h ^= s.charAt(i);
            h *= 0x100000001b3L;
        }
        return h;
    }
}
