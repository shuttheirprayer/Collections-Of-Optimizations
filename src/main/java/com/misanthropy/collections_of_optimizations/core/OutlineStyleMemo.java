package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.network.chat.Style;

public record OutlineStyleMemo(Style source, int color, Style result) {

    public boolean matches(Style style, int rgb) {
        return this.source == style && this.color == rgb;
    }
}
