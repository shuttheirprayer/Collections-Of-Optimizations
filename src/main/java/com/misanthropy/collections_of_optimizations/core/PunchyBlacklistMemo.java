package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.item.Item;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class PunchyBlacklistMemo {

    public static final Object NO_MATCH = new Object();

    private static volatile PunchyBlacklistMemo current;

    private final List<String> source;
    private final ConcurrentHashMap<Item, Object> matches = new ConcurrentHashMap<>();

    private PunchyBlacklistMemo(List<String> source) {
        this.source = source;
    }

    public static PunchyBlacklistMemo forSource(List<String> source) {
        PunchyBlacklistMemo memo = current;
        if (memo == null || memo.source != source) {
            memo = new PunchyBlacklistMemo(source);
            current = memo;
        }
        return memo;
    }

    public Object get(Item item) {
        return this.matches.get(item);
    }

    public void put(Item item, String match) {
        this.matches.put(item, match == null ? NO_MATCH : match);
    }
}
