package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import punchy.client.render.ToolTuning;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = ToolTuning.class, remap = false)
public abstract class MixinPunchyItemIdCache {

    @Unique
    private static final ConcurrentHashMap<Item, ResourceLocation> coo$itemIds = new ConcurrentHashMap<>();

    @Redirect(
            method = "idFor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;", remap = true),
            require = 0
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ResourceLocation coo$cachedItemId(DefaultedRegistry registry, Object value) {
        if (!CoOConfig.punchyCacheItemIds || !(value instanceof Item item)) {
            return registry.getKey(value);
        }
        ResourceLocation id = coo$itemIds.get(item);
        if (id == null) {
            id = registry.getKey(item);
            if (id != null) {
                coo$itemIds.put(item, id);
            }
        }
        return id;
    }
}
