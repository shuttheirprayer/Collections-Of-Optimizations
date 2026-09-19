package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public final class LethalityIds {

    private static final String[] BONUS_DROP_ENTITY_IDS = {
            "terramity:gob",
            "terramity:super_sniffer",
            "terramity:trial_guardian",
            "terramity:gundalf"
    };

    private static final String[] CHARM_ITEM_IDS = {
            "lethality:charged_redstone_piece",
            "lethality:backup_sos",
            "lethality:ultra_backup_sos"
    };

    private static volatile Set<EntityType<?>> bonusDropTypes;
    private static volatile Set<Item> charmItems;

    private LethalityIds() {
    }

    public static Set<EntityType<?>> bonusDropTypes() {
        Set<EntityType<?>> cached = bonusDropTypes;
        if (cached != null) {
            return cached;
        }
        Set<EntityType<?>> built = new ReferenceOpenHashSet<>(4);
        for (String id : BONUS_DROP_ENTITY_IDS) {
            try {
                EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
                if (type != null) {
                    built.add(type);
                }
            } catch (RuntimeException exception) {
                return built;
            }
        }
        bonusDropTypes = built;
        return built;
    }

    public static Set<Item> charmItems() {
        Set<Item> cached = charmItems;
        if (cached != null) {
            return cached;
        }
        Set<Item> built = new ReferenceOpenHashSet<>(4);
        for (String id : CHARM_ITEM_IDS) {
            try {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                if (item != null && item != Items.AIR) {
                    built.add(item);
                }
            } catch (RuntimeException exception) {
                return built;
            }
        }
        charmItems = built;
        return built;
    }
}
