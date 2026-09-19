package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.network.chat.Component;

public final class DoppelBossBarNames {

    private static final int SLOTS = 8;
    private static final String PREFIX = "Dark Doppelganger";
    private static final Component[] KEYS = new Component[SLOTS];
    private static final boolean[] VALUES = new boolean[SLOTS];

    private static int cursor;

    private DoppelBossBarNames() {
    }

    public static boolean matches(Component name) {
        if (name == null) {
            return false;
        }
        for (int i = 0; i < SLOTS; i++) {
            if (KEYS[i] == name) {
                return VALUES[i];
            }
        }
        boolean match;
        try {
            match = name.getString().startsWith(PREFIX);
        } catch (Throwable throwable) {
            return true;
        }
        int slot = cursor;
        KEYS[slot] = name;
        VALUES[slot] = match;
        cursor = slot + 1 >= SLOTS ? 0 : slot + 1;
        return match;
    }
}
