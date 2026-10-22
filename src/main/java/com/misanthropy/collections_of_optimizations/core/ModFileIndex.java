package com.misanthropy.collections_of_optimizations.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModFileIndex {

    private static final ModFileIndex BROKEN = new ModFileIndex(null, null);
    private static final Map<Path, ModFileIndex> BUILT = new ConcurrentHashMap<>();
    private static final Map<Path, Boolean> REQUESTED = new ConcurrentHashMap<>();

    private final Paths data;
    private final Paths assets;

    private ModFileIndex(Paths data, Paths assets) {
        this.data = data;
        this.assets = assets;
    }

    public boolean usable() {
        return data != null;
    }

    public boolean contains(String[] paths) {
        Paths tree = tree(paths[0]);
        StringBuilder relative = new StringBuilder();
        for (int i = 1; i < paths.length; i++) {
            if (i > 1) {
                relative.append('/');
            }
            relative.append(paths[i]);
        }
        return tree.contains(relative.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void list(String top, String namespace, List<String> parts, Consumer<String> output) {
        StringBuilder prefix = new StringBuilder(namespace).append('/');
        for (String part : parts) {
            prefix.append(part).append('/');
        }
        tree(top).list(prefix.toString().getBytes(StandardCharsets.UTF_8), namespace.length() + 1, output);
    }

    private Paths tree(String top) {
        return "data".equals(top) ? data : assets;
    }

    public static ModFileIndex cached(Path source) {
        return BUILT.get(source);
    }

    public static boolean indexed(String top) {
        return "assets".equals(top) || "data".equals(top);
    }

    public static ModFileIndex getOrBuild(Path source, Function<String[], Path> resolver) {
        ModFileIndex index = BUILT.get(source);
        if (index != null) {
            return index;
        }
        synchronized (lockFor(source)) {
            index = BUILT.get(source);
            if (index == null) {
                index = build(resolver);
                BUILT.put(source, index);
            }
            return index;
        }
    }

    public static void buildInBackground(Path source, Function<String[], Path> resolver) {
        if (BUILT.containsKey(source) || REQUESTED.putIfAbsent(source, Boolean.TRUE) != null) {
            return;
        }
        Workers.POOL.execute(() -> getOrBuild(source, resolver));
    }

    private static Object lockFor(Path source) {
        return LOCKS.computeIfAbsent(source, k -> new Object());
    }

    private static final Map<Path, Object> LOCKS = new ConcurrentHashMap<>();

    static ModFileIndex build(Function<String[], Path> resolver) {
        try {
            return new ModFileIndex(walk(resolver, "data"), walk(resolver, "assets"));
        } catch (IOException | RuntimeException e) {
            return BROKEN;
        }
    }

    private static Paths walk(Function<String[], Path> resolver, String top) throws IOException {
        Path root = resolver.apply(new String[]{top});
        List<byte[]> found = new ArrayList<>();
        if (Files.isDirectory(root)) {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!attrs.isRegularFile()) {
                        return FileVisitResult.CONTINUE;
                    }
                    Path relative = root.relativize(file);
                    StringBuilder joined = new StringBuilder();
                    String[] segments = new String[relative.getNameCount() + 1];
                    segments[0] = top;
                    for (int i = 0; i < relative.getNameCount(); i++) {
                        String name = relative.getName(i).toString();
                        segments[i + 1] = name;
                        if (i > 0) {
                            joined.append('/');
                        }
                        joined.append(name);
                    }
                    if (found.isEmpty() && !resolver.apply(segments).toString().equals(file.toString())) {
                        throw new IllegalStateException();
                    }
                    found.add(joined.toString().getBytes(StandardCharsets.UTF_8));
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return Paths.of(found);
    }

    private static final class Paths {

        private final byte[] blob;
        private final int[] starts;

        private Paths(byte[] blob, int[] starts) {
            this.blob = blob;
            this.starts = starts;
        }

        static Paths of(List<byte[]> entries) {
            byte[][] sorted = entries.toArray(new byte[0][]);
            Arrays.sort(sorted, Arrays::compareUnsigned);
            int total = 0;
            for (byte[] entry : sorted) {
                total += entry.length;
            }
            byte[] blob = new byte[total];
            int[] starts = new int[sorted.length + 1];
            int at = 0;
            for (int i = 0; i < sorted.length; i++) {
                starts[i] = at;
                System.arraycopy(sorted[i], 0, blob, at, sorted[i].length);
                at += sorted[i].length;
            }
            starts[sorted.length] = at;
            return new Paths(blob, starts);
        }

        int size() {
            return starts.length - 1;
        }

        int compare(int index, byte[] key) {
            return Arrays.compareUnsigned(blob, starts[index], starts[index + 1], key, 0, key.length);
        }

        int lowerBound(byte[] key) {
            int low = 0;
            int high = size();
            while (low < high) {
                int middle = (low + high) >>> 1;
                if (compare(middle, key) < 0) {
                    low = middle + 1;
                } else {
                    high = middle;
                }
            }
            return low;
        }

        boolean contains(byte[] key) {
            int at = lowerBound(key);
            return at < size() && compare(at, key) == 0;
        }

        void list(byte[] prefix, int skip, Consumer<String> output) {
            for (int i = lowerBound(prefix); i < size(); i++) {
                int start = starts[i];
                int length = starts[i + 1] - start;
                if (length < prefix.length || Arrays.mismatch(blob, start, start + prefix.length, prefix, 0, prefix.length) != -1) {
                    return;
                }
                output.accept(new String(blob, start + skip, length - skip, StandardCharsets.UTF_8));
            }
        }
    }

    private static final class Workers {

        private static final AtomicInteger COUNTER = new AtomicInteger();

        private static final ExecutorService POOL = Executors.newFixedThreadPool(
                Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors() / 4)),
                task -> {
                    Thread thread = new Thread(task, "CoO mod file index " + COUNTER.incrementAndGet());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                });
    }
}
