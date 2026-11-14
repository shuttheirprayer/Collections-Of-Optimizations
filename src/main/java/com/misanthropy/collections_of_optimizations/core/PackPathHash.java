package com.misanthropy.collections_of_optimizations.core;

public final class PackPathHash {

    private static final long OFFSET = 0xcbf29ce484222325L;
    private static final long PRIME = 0x100000001b3L;

    private PackPathHash() {
    }

    public static long of(byte[] bytes, int start, int end) {
        long h = OFFSET;
        for (int i = start; i < end; i++) {
            int b = bytes[i];
            if (b < 0) {
                return 0L;
            }
            h = (h ^ b) * PRIME;
        }
        return finish(h);
    }

    public static long of(String namespace, String path) {
        long h = OFFSET;
        for (int i = 0, n = namespace.length(); i < n; i++) {
            char c = namespace.charAt(i);
            if (c >= 0x80) {
                return 0L;
            }
            h = (h ^ c) * PRIME;
        }
        h = (h ^ '/') * PRIME;
        for (int i = 0, n = path.length(); i < n; i++) {
            char c = path.charAt(i);
            if (c >= 0x80) {
                return 0L;
            }
            h = (h ^ c) * PRIME;
        }
        return finish(h);
    }

    private static long finish(long h) {
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= h >>> 33;
        return h == 0L ? 1L : h;
    }
}
