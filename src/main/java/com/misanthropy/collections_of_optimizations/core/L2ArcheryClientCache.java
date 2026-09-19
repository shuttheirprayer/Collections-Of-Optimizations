package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class L2ArcheryClientCache {

    private static final String BOW_CLASS = "dev.xkmc.l2archery.content.item.GenericBowItem";
    private static final int MAX_TEXTURES = 64;

    private static final Map<String, ResourceLocation> TEXTURES = new ConcurrentHashMap<>();

    private static volatile Class<?> bowClass;
    private static volatile boolean bowClassResolved;

    private static ItemStack infoBow = ItemStack.EMPTY;
    private static ItemStack infoArrow = ItemStack.EMPTY;
    private static List<Component> infoText;

    private L2ArcheryClientCache() {
    }

    public static ResourceLocation texture(String namespace, String path) {
        if (namespace == null || path == null) {
            return null;
        }
        ResourceLocation cached = TEXTURES.get(namespace);
        if (cached != null && cached.getPath().equals(path)) {
            return cached;
        }
        return null;
    }

    public static void putTexture(String namespace, ResourceLocation value) {
        if (namespace == null || value == null) {
            return;
        }
        if (TEXTURES.size() >= MAX_TEXTURES) {
            TEXTURES.clear();
        }
        TEXTURES.put(namespace, value);
    }

    public static boolean isGenericBow(Item item) {
        if (item == null) {
            return false;
        }
        if (!bowClassResolved) {
            bowClassResolved = true;
            try {
                bowClass = Class.forName(BOW_CLASS, false, L2ArcheryClientCache.class.getClassLoader());
            } catch (Throwable ignored) {
                bowClass = null;
            }
        }
        Class<?> resolved = bowClass;
        return resolved != null && resolved.isInstance(item);
    }

    public static List<Component> infoText(ItemStack bow, ItemStack arrow) {
        List<Component> text = infoText;
        if (text == null || bow == null || arrow == null) {
            return null;
        }
        if (!ItemStack.matches(infoBow, bow) || !ItemStack.matches(infoArrow, arrow)) {
            return null;
        }
        return text;
    }

    public static void putInfoText(ItemStack bow, ItemStack arrow, List<Component> text) {
        if (bow == null || arrow == null || text == null) {
            return;
        }
        List<Component> snapshot;
        try {
            snapshot = List.copyOf(text);
        } catch (Throwable ignored) {
            return;
        }
        infoBow = bow.copy();
        infoArrow = arrow.copy();
        infoText = snapshot;
    }
}
