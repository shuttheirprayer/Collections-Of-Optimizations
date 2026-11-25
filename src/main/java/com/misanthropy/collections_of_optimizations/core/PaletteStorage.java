package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.util.BitStorage;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.level.chunk.Palette;

import java.util.Arrays;
import java.util.function.Consumer;

public final class PaletteStorage {

    private static final int MAX_BITS = 8;
    private static final ThreadLocal<int[]> REMAP = ThreadLocal.withInitial(() -> new int[1 << MAX_BITS]);

    private PaletteStorage() {
    }

    public static <T> boolean copy(Palette<T> from, BitStorage source, Palette<T> to, BitStorage target) {
        if (source == target || target.getClass() != SimpleBitStorage.class || target.getSize() != source.getSize()) {
            return false;
        }
        if (source.getClass() == ZeroBitStorage.class) {
            return fill(target, to.idFor(from.valueFor(0)));
        }
        int sourceBits = source.getBits();
        if (source.getClass() != SimpleBitStorage.class || sourceBits > MAX_BITS) {
            return false;
        }
        int[] remap = REMAP.get();
        Arrays.fill(remap, 0, 1 << sourceBits, -1);
        long[] sourceWords = source.getRaw();
        long[] targetWords = target.getRaw();
        long sourceMask = (1L << sourceBits) - 1L;
        int sourcePerWord = 64 / sourceBits;
        int targetBits = target.getBits();
        int targetPerWord = 64 / targetBits;
        int size = source.getSize();
        long sourceWord = 0L;
        long word = 0L;
        int sourceIndex = 0;
        int sourceLeft = 0;
        int targetIndex = 0;
        int slot = 0;
        for (int i = 0; i < size; i++) {
            if (sourceLeft == 0) {
                sourceWord = sourceWords[sourceIndex++];
                sourceLeft = sourcePerWord;
            }
            int oldId = (int) (sourceWord & sourceMask);
            sourceWord >>>= sourceBits;
            sourceLeft--;
            int id = remap[oldId];
            if (id < 0) {
                id = to.idFor(from.valueFor(oldId));
                if (id < 0 || id >>> targetBits != 0) {
                    return false;
                }
                remap[oldId] = id;
            }
            word |= (long) id << slot * targetBits;
            if (++slot == targetPerWord) {
                targetWords[targetIndex++] = word;
                word = 0L;
                slot = 0;
            }
        }
        if (slot > 0) {
            targetWords[targetIndex] = word;
        }
        return true;
    }

    private static boolean fill(BitStorage target, int id) {
        int bits = target.getBits();
        if (id < 0 || id >>> bits != 0) {
            return false;
        }
        long[] words = target.getRaw();
        int perWord = 64 / bits;
        long word = 0L;
        for (int slot = 0; slot < perWord; slot++) {
            word |= (long) id << slot * bits;
        }
        int size = target.getSize();
        int full = size / perWord;
        Arrays.fill(words, 0, full, word);
        int rest = size - full * perWord;
        if (rest > 0) {
            words[full] = word & (1L << rest * bits) - 1L;
        }
        return true;
    }

    public static <T> boolean forEachValue(Palette<T> palette, BitStorage storage, Consumer<T> consumer) {
        if (storage.getClass() == ZeroBitStorage.class) {
            consumer.accept(palette.valueFor(0));
            return true;
        }
        int bits = storage.getBits();
        if (storage.getClass() != SimpleBitStorage.class || bits > 6) {
            return false;
        }
        long mask = (1L << bits) - 1L;
        int perWord = 64 / bits;
        int left = storage.getSize();
        long seen = 0L;
        byte[] order = new byte[1 << bits];
        int count = 0;
        for (long word : storage.getRaw()) {
            for (int slot = Math.min(perWord, left); slot > 0; slot--) {
                int id = (int) (word & mask);
                word >>>= bits;
                long bit = 1L << id;
                if ((seen & bit) == 0L) {
                    seen |= bit;
                    order[count++] = (byte) id;
                }
            }
            left -= perWord;
        }
        for (int i = 0; i < count; i++) {
            consumer.accept(palette.valueFor(order[i]));
        }
        return true;
    }
}
