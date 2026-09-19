package com.misanthropy.collections_of_optimizations.core;

import java.lang.reflect.Field;

public final class LmBossMusicState {

    private static final String MESSAGE_CLASS = "net.miauczel.legendary_monsters.Message.PlayBossMusicMessage";

    private static volatile Field playField;

    private static volatile boolean resolved;

    private LmBossMusicState() {
    }

    public static int stateOf(Object message) {
        if (message == null || !MESSAGE_CLASS.equals(message.getClass().getName())) {
            return -1;
        }
        Field field = playField;
        if (field == null) {
            if (resolved) {
                return -1;
            }
            field = resolve(message.getClass());
            if (field == null) {
                return -1;
            }
        }
        try {
            return field.getBoolean(message) ? 1 : 0;
        } catch (Throwable throwable) {
            return -1;
        }
    }

    private static synchronized Field resolve(Class<?> type) {
        if (resolved) {
            return playField;
        }
        resolved = true;
        try {
            Field candidate = type.getDeclaredField("play");
            if (candidate.getType() == boolean.class) {
                candidate.setAccessible(true);
                playField = candidate;
            }
        } catch (Throwable throwable) {
            playField = null;
        }
        return playField;
    }
}
