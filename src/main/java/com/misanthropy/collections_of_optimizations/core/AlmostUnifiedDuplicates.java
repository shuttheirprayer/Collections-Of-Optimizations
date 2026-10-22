package com.misanthropy.collections_of_optimizations.core;

import com.almostreliable.unified.config.DuplicationConfig;
import com.almostreliable.unified.recipe.RecipeLink;
import com.almostreliable.unified.utils.JsonCompare;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AlmostUnifiedDuplicates {

    private AlmostUnifiedDuplicates() {
    }

    public static Set<RecipeLink.DuplicateLink> handle(DuplicationConfig config, Collection<RecipeLink> recipeLinks, List<RecipeLink> linksToCompare) {
        Set<RecipeLink.DuplicateLink> duplicates = new HashSet<>(recipeLinks.size());
        if (linksToCompare.isEmpty()) {
            return duplicates;
        }
        JsonCompare.CompareSettings settings = config.getCompareSettings(linksToCompare.get(0).getType());
        JsonElement sanitize = settings.serialize().get(JsonCompare.CompareSettings.SHOULD_SANITIZE);
        if (sanitize == null || sanitize.getAsBoolean()) {
            return null;
        }
        Set<String> ignored = settings.getIgnoredFields();
        Map<RecipeLink, Integer> signatures = new IdentityHashMap<>(linksToCompare.size());
        Int2ObjectOpenHashMap<List<RecipeLink>> buckets = new Int2ObjectOpenHashMap<>();
        for (RecipeLink link : linksToCompare) {
            int signature = signature(link.getActual(), ignored);
            signatures.put(link, signature);
            if (!config.shouldIgnoreRecipe(link)) {
                buckets.computeIfAbsent(signature, k -> new ArrayList<>(2)).add(link);
            }
        }
        for (RecipeLink current : recipeLinks) {
            if (config.shouldIgnoreRecipe(current)) {
                continue;
            }
            Integer signature = signatures.get(current);
            List<RecipeLink> bucket = buckets.get(signature != null ? signature : signature(current.getActual(), ignored));
            if (bucket == null || bucket.size() < 2 && bucket.get(0) == current) {
                continue;
            }
            JsonCompare.CompareContext context = config.getCompareContext(current);
            boolean found = false;
            for (RecipeLink other : bucket) {
                if (other != current) {
                    found |= current.handleDuplicate(other, context);
                }
            }
            if (found && current.getDuplicateLink() != null) {
                duplicates.add(current.getDuplicateLink());
            }
        }
        return duplicates;
    }

    static int signature(JsonObject recipe, Set<String> ignored) {
        int hash = 0;
        for (Map.Entry<String, JsonElement> entry : recipe.entrySet()) {
            if (!ignored.contains(entry.getKey())) {
                hash += entry.getKey().hashCode() ^ hash(entry.getValue());
            }
        }
        return hash;
    }

    static int hash(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return 0;
        }
        if (element instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
                double value = primitive.getAsNumber().doubleValue();
                return Double.hashCode(value == 0.0 ? 0.0 : value);
            }
            return primitive.isBoolean() ? Boolean.hashCode(primitive.getAsBoolean()) : primitive.getAsString().hashCode();
        }
        if (element instanceof JsonArray array) {
            int hash = 1;
            for (JsonElement item : array) {
                hash = 31 * hash + hash(item);
            }
            return hash;
        }
        int hash = 7;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            hash += entry.getKey().hashCode() ^ hash(entry.getValue());
        }
        return hash;
    }
}
