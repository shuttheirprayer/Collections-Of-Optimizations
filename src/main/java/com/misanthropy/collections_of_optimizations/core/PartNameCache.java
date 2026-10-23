package com.misanthropy.collections_of_optimizations.core;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public final class PartNameCache<V> extends AbstractMap<String, V> {

    private static final int REMEMBERED_MAPS = 4;
    private static final int NAMES = 32;
    private static final PartNameCache<?>[] RECENT = new PartNameCache<?>[REMEMBERED_MAPS];
    private static int next;

    private final Map<String, V> delegate;
    private final String[] names = new String[NAMES];
    private final Object[] parts = new Object[NAMES];
    private int count;

    private PartNameCache(Map<String, V> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static <V> Map<String, V> of(Map<String, V> delegate) {
        for (PartNameCache<?> cache : RECENT) {
            if (cache != null && cache.delegate == delegate) return (Map<String, V>) cache;
        }
        PartNameCache<V> cache = new PartNameCache<>(delegate);
        RECENT[next] = cache;
        next = (next + 1) % REMEMBERED_MAPS;
        return cache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        for (int i = 0; i < count; i++) {
            if (names[i] == key) return (V) parts[i];
        }
        V part = delegate.get(key);
        if (part != null && count < NAMES && key instanceof String name) {
            names[count] = name;
            parts[count] = part;
            count++;
        }
        return part;
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return delegate.entrySet();
    }
}
