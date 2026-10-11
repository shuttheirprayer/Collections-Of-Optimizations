package com.misanthropy.collections_of_optimizations.mixin.itemfilters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ItemFilterTagCache;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.latvian.mods.itemfilters.item.TagFilterItem", remap = false)
public abstract class MixinTagFilterItemTagCache {

    @Redirect(
            method = {"filter", "filterItem"},
            at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0
    )
    private ResourceLocation coo$cachedLocation(String value) {
        if (!CoOConfig.itemfiltersCacheTagFilterKeys) {
            return new ResourceLocation(value);
        }
        return ItemFilterTagCache.location(value);
    }

    @Redirect(
            method = {"filter", "filterItem"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tags/TagKey;create(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;", remap = true),
            require = 0
    )
    private <T> TagKey<T> coo$cachedTag(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
        if (!CoOConfig.itemfiltersCacheTagFilterKeys) {
            return TagKey.create(registry, location);
        }
        return ItemFilterTagCache.tag(registry, location);
    }
}
