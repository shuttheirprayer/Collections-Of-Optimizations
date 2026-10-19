package com.misanthropy.collections_of_optimizations.core;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.slf4j.Logger;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TerrablenderStackedLayers {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Codec<Map<String, SurfaceRules.RuleSource>> SOURCES = Codec.unboundedMap(Codec.STRING, SurfaceRules.RuleSource.CODEC);

    private TerrablenderStackedLayers() {
    }

    public static SurfaceRules.RuleSource dedupe(SurfaceRules.RuleSource built) {
        try {
            return dedupeOrThrow(built);
        } catch (Throwable throwable) {
            LOGGER.warn("Collections of Optimizations: left TerraBlender surface rule layers as they are ({})", throwable.toString());
            return built;
        }
    }

    private static SurfaceRules.RuleSource dedupeOrThrow(SurfaceRules.RuleSource built) {
        List<NamespacedSurfaceRuleSource> kept = new ArrayList<>();
        List<JsonElement> seen = new ArrayList<>();
        SurfaceRules.RuleSource node = built;
        int total = 0;
        while (node instanceof NamespacedSurfaceRuleSource layer) {
            total++;
            JsonElement encoded = SOURCES.encodeStart(JsonOps.INSTANCE, layer.sources()).result().orElse(null);
            if (encoded == null) {
                return built;
            }
            if (!seen.contains(encoded)) {
                seen.add(encoded);
                kept.add(layer);
            }
            node = layer.base();
        }
        if (kept.size() == total) {
            return built;
        }
        for (int i = kept.size() - 1; i >= 0; i--) {
            node = new NamespacedSurfaceRuleSource(node, kept.get(i).sources());
        }
        LOGGER.info("Collections of Optimizations: TerraBlender surface rules were stacked.. {} deep from earlier world loads, {} of the layers are distinct and kept. This is a good thing do NOT worry!", total, kept.size());
        return node;
    }
}
