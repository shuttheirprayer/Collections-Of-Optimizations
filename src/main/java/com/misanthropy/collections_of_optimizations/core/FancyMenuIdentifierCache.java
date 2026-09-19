package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

public final class FancyMenuIdentifierCache {

    public static final byte UNKNOWN = -1;

    private static final String NULL = new String(" coo$null");

    private static final Object2ByteOpenHashMap<String> VALID = new Object2ByteOpenHashMap<>(256);
    private static final Object2ByteOpenHashMap<String> BLACKLISTED = new Object2ByteOpenHashMap<>(256);
    private static final Object2ObjectOpenHashMap<String, String> BEST = new Object2ObjectOpenHashMap<>(256);
    private static final Object2ObjectOpenHashMap<String, String> UNIVERSAL = new Object2ObjectOpenHashMap<>(256);

    private static volatile boolean dirty;

    static {
        VALID.defaultReturnValue(UNKNOWN);
        BLACKLISTED.defaultReturnValue(UNKNOWN);
    }

    private FancyMenuIdentifierCache() {
    }

    public static boolean usable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || !mc.isSameThread()) {
            return false;
        }
        if (dirty) {
            dirty = false;
            VALID.clear();
            BLACKLISTED.clear();
            BEST.clear();
            UNIVERSAL.clear();
        }
        return true;
    }

    public static byte getValid(String identifier) {
        return VALID.getByte(identifier);
    }

    public static void putValid(String identifier, boolean valid) {
        VALID.put(identifier, valid ? (byte) 1 : (byte) 0);
    }

    public static byte getBlacklisted(String identifier) {
        return BLACKLISTED.getByte(identifier);
    }

    public static void putBlacklisted(String identifier, boolean blacklisted) {
        BLACKLISTED.put(identifier, blacklisted ? (byte) 1 : (byte) 0);
    }

    public static String getBest(String identifier) {
        return BEST.get(identifier);
    }

    public static void putBest(String identifier, String best) {
        BEST.put(identifier, best == null ? NULL : best);
    }

    public static String getUniversal(String identifier) {
        return UNIVERSAL.get(identifier);
    }

    public static void putUniversal(String identifier, String universal) {
        UNIVERSAL.put(identifier, universal == null ? NULL : universal);
    }

    public static boolean isNull(String cached) {
        return cached == NULL;
    }

    public static void invalidate() {
        dirty = true;
    }
}
