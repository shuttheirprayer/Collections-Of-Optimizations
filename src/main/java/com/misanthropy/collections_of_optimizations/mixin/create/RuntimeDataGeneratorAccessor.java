package com.misanthropy.collections_of_optimizations.mixin.create;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.data.RuntimeDataGenerator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RuntimeDataGenerator.class, remap = false)
public interface RuntimeDataGeneratorAccessor {

    @Invoker("cuttingRecipes")
    static void coo$cuttingRecipes(ResourceLocation itemId) {
        throw new AssertionError();
    }

    @Invoker("washingRecipes")
    static void coo$washingRecipes(ResourceLocation itemId) {
        throw new AssertionError();
    }

    @Accessor("JSON_FILES")
    static Object2ObjectOpenHashMap<ResourceLocation, JsonObject> coo$jsonFiles() {
        throw new AssertionError();
    }

    @Accessor("TAGS")
    static Multimap<ResourceLocation, TagEntry> coo$tags() {
        throw new AssertionError();
    }
}
