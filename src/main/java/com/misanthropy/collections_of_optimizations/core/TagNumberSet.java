package com.misanthropy.collections_of_optimizations.core;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class TagNumberSet {

    private static final Class<?> SET_12 = Set.of(1).getClass();
    private static final Class<?> SET_N = Set.of(1, 2, 3).getClass();
    private static final ConcurrentHashMap<Object, Integer> NUMBERS = new ConcurrentHashMap<>();
    private static final AtomicInteger NEXT = new AtomicInteger();
    private static final int SCAN_LIMIT = 8;

    public final Set<?> source;
    private final int[] numbers;
    private final long summary;

    private TagNumberSet(Set<?> source, int[] numbers) {
        this.source = source;
        this.numbers = numbers;
        long summary = 0L;
        for (int number : numbers) {
            summary |= 1L << number;
        }
        this.summary = summary;
    }

    public static int number(TagKeyNumber key) {
        int number = key.coo$tagNumber();
        if (number == 0) {
            number = NUMBERS.computeIfAbsent(key, k -> NEXT.incrementAndGet());
            key.coo$tagNumber(number);
        }
        return number;
    }

    public static TagNumberSet of(Set<?> tags) {
        Class<?> type = tags.getClass();
        if (type != SET_N && type != SET_12) {
            return null;
        }
        int[] numbers = new int[tags.size()];
        int count = 0;
        for (Object tag : tags) {
            if (!(tag instanceof TagKeyNumber key) || count == numbers.length) {
                return null;
            }
            numbers[count++] = number(key);
        }
        if (count != numbers.length) {
            return null;
        }
        Arrays.sort(numbers);
        return new TagNumberSet(tags, numbers);
    }

    public boolean has(int number) {
        if ((this.summary & 1L << number) == 0L) {
            return false;
        }
        int[] numbers = this.numbers;
        if (numbers.length <= SCAN_LIMIT) {
            for (int candidate : numbers) {
                if (candidate == number) {
                    return true;
                }
            }
            return false;
        }
        return Arrays.binarySearch(numbers, number) >= 0;
    }
}
