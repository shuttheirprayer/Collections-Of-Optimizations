package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class L2CurioSlotTags {

    private static final Map<String, TagKey<Item>> TAGS = new ConcurrentHashMap<>();

    private L2CurioSlotTags() {
    }

    public static boolean hasItem(LivingEntity entity, Item item) {
        Optional<ICuriosItemHandler> inventory = CuriosApi.getCuriosInventory(entity).resolve();
        if (inventory.isEmpty()) {
            return false;
        }
        for (ICurioStacksHandler handler : inventory.get().getCurios().values()) {
            IDynamicStackHandler stacks = handler.getStacks();
            if (stacks.getSlots() == 0) {
                continue;
            }
            String identifier = handler.getIdentifier();
            if (!identifier.equals("curio") && !item.builtInRegistryHolder().is(tag(identifier))) {
                continue;
            }
            for (int i = 0; i < stacks.getSlots(); i++) {
                if (stacks.getStackInSlot(i).is(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static TagKey<Item> tag(String identifier) {
        TagKey<Item> tag = TAGS.get(identifier);
        if (tag == null) {
            tag = ItemTags.create(new ResourceLocation("curios", identifier));
            TAGS.put(identifier, tag);
        }
        return tag;
    }
}
