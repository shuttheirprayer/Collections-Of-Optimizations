package com.misanthropy.collections_of_optimizations.core;

public final class DrawersCountLabelCache {

    private static final int MASK = 255;

    private static final class Table {
        final String[] formatKeys = new String[MASK + 1];
        final int[] formatArgBits = new int[MASK + 1];
        final String[] formatValues = new String[MASK + 1];
        final int[] plainKeys = new int[MASK + 1];
        final String[] plainValues = new String[MASK + 1];
    }

    private static final ThreadLocal<Table> TABLES = ThreadLocal.withInitial(Table::new);

    private DrawersCountLabelCache() {
    }

    public static String lookupFormatted(String format, Object[] args) {
        if (!usable(format, args)) {
            return null;
        }
        int bits = Float.floatToRawIntBits((Float) args[0]);
        Table table = TABLES.get();
        int slot = formatSlot(format, bits);
        String value = table.formatValues[slot];
        if (value != null && table.formatKeys[slot] == format && table.formatArgBits[slot] == bits) {
            return value;
        }
        return null;
    }

    public static void storeFormatted(String format, Object[] args, String text) {
        if (text == null || !usable(format, args)) {
            return;
        }
        int bits = Float.floatToRawIntBits((Float) args[0]);
        Table table = TABLES.get();
        int slot = formatSlot(format, bits);
        table.formatKeys[slot] = format;
        table.formatArgBits[slot] = bits;
        table.formatValues[slot] = text;
    }

    public static String lookupPlain(int count) {
        Table table = TABLES.get();
        int slot = plainSlot(count);
        String value = table.plainValues[slot];
        if (value != null && table.plainKeys[slot] == count) {
            return value;
        }
        return null;
    }

    public static void storePlain(int count, String text) {
        if (text == null) {
            return;
        }
        Table table = TABLES.get();
        int slot = plainSlot(count);
        table.plainKeys[slot] = count;
        table.plainValues[slot] = text;
    }

    private static boolean usable(String format, Object[] args) {
        return format != null && args != null && args.length == 1 && args[0] instanceof Float;
    }

    private static int formatSlot(String format, int bits) {
        int hash = System.identityHashCode(format) * 31 + bits;
        hash ^= hash >>> 16;
        return hash & MASK;
    }

    private static int plainSlot(int count) {
        int hash = count * 0x9E3779B9;
        hash ^= hash >>> 16;
        return hash & MASK;
    }
}
