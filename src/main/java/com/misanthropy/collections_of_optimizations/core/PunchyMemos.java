package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PunchyMemos {

    private static final Object NONE = new Object();

    private static final Reference2ObjectOpenHashMap<Object, Object> MERGED = new Reference2ObjectOpenHashMap<>();
    private static Object mergedDefaults;

    private static final ConcurrentHashMap<Class<?>, Boolean> SPEAR_CLASSES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Item, Boolean> SPEAR_IDS = new ConcurrentHashMap<>();

    private static volatile ActiveOptions activeOptions;

    private PunchyMemos() {
    }

    public static Set<String> activeOptions(List<String> source) {
        ActiveOptions known = activeOptions;
        return known != null && known.source == source ? known.options : null;
    }

    public static void rememberActiveOptions(List<String> source, Set<String> options) {
        activeOptions = new ActiveOptions(source, options);
    }

    public static synchronized Object merged(Object primary, Object defaults) {
        if (defaults != mergedDefaults) {
            return null;
        }
        return MERGED.get(primary == null ? NONE : primary);
    }

    public static synchronized void rememberMerged(Object primary, Object defaults, Object result) {
        if (defaults != mergedDefaults) {
            MERGED.clear();
            mergedDefaults = defaults;
        }
        MERGED.put(primary == null ? NONE : primary, result == null ? NONE : result);
    }

    public static synchronized void forgetMerged() {
        MERGED.clear();
        mergedDefaults = null;
    }

    public static Object unwrapMerged(Object value) {
        return value == NONE ? null : value;
    }

    public static Boolean spearClass(Class<?> type) {
        return SPEAR_CLASSES.get(type);
    }

    public static void rememberSpearClass(Class<?> type, boolean spear) {
        SPEAR_CLASSES.put(type, spear);
    }

    public static Boolean spearId(Item item) {
        return SPEAR_IDS.get(item);
    }

    public static void rememberSpearId(Item item, boolean spear) {
        SPEAR_IDS.put(item, spear);
    }

    private record ActiveOptions(List<String> source, Set<String> options) {
    }
}
