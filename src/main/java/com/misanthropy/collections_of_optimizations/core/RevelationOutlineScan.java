package com.misanthropy.collections_of_optimizations.core;

import com.mega.endinglib.api.client.text.TextColorInterface;
import com.mega.revelationfix.client.enums.ModChatFormatting;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

public final class RevelationOutlineScan {

    private static final Int2CharOpenHashMap NONE = new Int2CharOpenHashMap();

    private static final FormattedCharSink SCAN = (index, style, codePoint) -> {
        TextColor colour = style.getColor();
        return colour == null || !isOutline(((TextColorInterface) (Object) colour).endinglib$getCode());
    };

    private RevelationOutlineScan() {
    }

    private static boolean isOutline(char code) {
        ChatFormatting apollyon = ModChatFormatting.APOLLYON;
        ChatFormatting eden = ModChatFormatting.EDEN;
        return (apollyon != null && code == apollyon.getChar()) || (eden != null && code == eden.getChar());
    }

    public static boolean needsOutline(FormattedCharSequence text) {
        return !text.accept(SCAN);
    }

    public static Int2CharOpenHashMap none() {
        return NONE;
    }
}
