package com.misanthropy.collections_of_optimizations.core;

import snownee.textanimator.effect.EffectFactory;

import java.util.Set;

public final class TextAnimatorTagFilter {

    public static final String[] NO_TAG = new String[0];

    public static final CoOStacklessArgumentException UNKNOWN_EFFECT_TYPE =
            new CoOStacklessArgumentException("Unknown effect type");

    private TextAnimatorTagFilter() {
    }

    public static boolean mayBeEffect(String content) {
        if (content == null) {
            return true;
        }
        int length = content.length();
        int start = 0;
        while (start < length && content.charAt(start) == ' ') {
            start++;
        }
        if (start >= length) {
            return true;
        }
        if (content.charAt(start) == '/') {
            return true;
        }
        int end = content.indexOf(' ', start);
        if (end < 0) {
            end = length;
        }
        Set<String> types = EffectFactory.listTypes();
        if (types == null || types.isEmpty()) {
            return true;
        }
        return types.contains(content.substring(start, end));
    }

    public static boolean isKnownType(String type) {
        Set<String> types = EffectFactory.listTypes();
        return types == null || types.isEmpty() || types.contains(type);
    }
}
