package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;

public final class ArmorModelBake {

    private static final Map<ModelLayerLocation, ModelPart> CACHE = new HashMap<>();

    private static int depth;

    private ArmorModelBake() {
    }

    public static void enter() {
        if (onRenderThread()) {
            depth++;
        }
    }

    public static void exit() {
        if (depth > 0 && onRenderThread()) {
            depth--;
        }
    }

    public static boolean active() {
        return depth > 0 && onRenderThread();
    }

    public static ModelPart get(ModelLayerLocation layer) {
        return CACHE.get(layer);
    }

    public static void put(ModelLayerLocation layer, ModelPart part) {
        CACHE.put(layer, part);
    }

    public static void clear() {
        CACHE.clear();
        depth = 0;
    }

    private static boolean onRenderThread() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.isSameThread();
    }
}
