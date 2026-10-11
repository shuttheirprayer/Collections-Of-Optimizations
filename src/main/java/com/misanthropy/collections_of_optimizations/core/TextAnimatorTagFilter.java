package com.misanthropy.collections_of_optimizations.core;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import snownee.textanimator.effect.Effect;
import snownee.textanimator.effect.EffectFactory;
import snownee.textanimator.effect.params.EmptyParams;
import snownee.textanimator.effect.params.Params;
import snownee.textanimator.effect.params.TypedParams;

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

    public static Effect createEffect(String[] split, boolean allowTypewriter) {
        if (split.length == 0) {
            throw new IllegalArgumentException("Empty input");
        }
        if (!allowTypewriter && "typewriter".equals(split[0])) {
            throw new IllegalArgumentException("Typewriter effect is not allowed here");
        }
        Params params = EmptyParams.INSTANCE;
        if (split.length > 1) {
            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builderWithExpectedSize(split.length - 1);
            for (int i = 1; i < split.length; i++) {
                String[] kv = StringUtils.split(split[i], "=", 2);
                if (kv.length == 1) {
                    builder.put(kv[0], true);
                } else if ("true".equals(kv[1]) || "false".equals(kv[1])) {
                    builder.put(kv[0], Boolean.parseBoolean(kv[1]));
                } else {
                    builder.put(kv[0], paramValue(kv[1]));
                }
            }
            params = new TypedParams(builder.build());
        }
        return EffectFactory.create(split[0], params);
    }

    private static Object paramValue(String raw) {
        if (!mayBeNumber(raw)) {
            return raw;
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return raw;
        }
    }

    private static boolean mayBeNumber(String raw) {
        int length = raw.length();
        int start = 0;
        while (start < length && raw.charAt(start) <= ' ') {
            start++;
        }
        if (start >= length) {
            return false;
        }
        char first = raw.charAt(start);
        return first >= '0' && first <= '9' || first == '+' || first == '-' || first == '.' || first == 'N' || first == 'I';
    }

    public static boolean isKnownType(String type) {
        Set<String> types = EffectFactory.listTypes();
        return types == null || types.isEmpty() || types.contains(type);
    }
}
