package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class OpposingForceStealthCache {

    private static final ResourceLocation STEALTH_ID = new ResourceLocation("opposing_force", "stealth");

    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    private static Attribute stealthAttribute;
    private static final TagKey<Item> RAMBLER_SKULLS = TagKey.create(Registries.ITEM, new ResourceLocation("opposing_force", "rambler_skulls"));
    private static Map<EntityType<?>, Predicate<ItemStack>> disguises;

    private OpposingForceStealthCache() {
    }

    public static double stealth(LivingEntity entity) {
        if (entity == null) {
            return 0.0D;
        }
        Attribute attribute = attribute();
        if (attribute == null) {
            return 0.0D;
        }
        if (entity instanceof OpposingForceStealthHolder holder) {
            int stamp = entity.tickCount + 1;
            if (holder.coo$opposingForceStealthStamp() == stamp) {
                return holder.coo$opposingForceStealth();
            }
            double value = compute(entity, attribute);
            holder.coo$storeOpposingForceStealth(value, stamp);
            return value;
        }
        return compute(entity, attribute);
    }

    private static Attribute attribute() {
        Attribute cached = stealthAttribute;
        if (cached != null) {
            return cached;
        }
        Attribute resolved = ForgeRegistries.ATTRIBUTES.getValue(STEALTH_ID);
        if (resolved != null) {
            stealthAttribute = resolved;
        }
        return resolved;
    }

    public static boolean disguised(LivingEntity entity, Entity looking) {
        Map<EntityType<?>, Predicate<ItemStack>> map = disguises();
        if (map.isEmpty()) {
            return false;
        }
        Predicate<ItemStack> head = map.get(looking.getType());
        return head != null && head.test(entity.getItemBySlot(EquipmentSlot.HEAD));
    }

    private static Map<EntityType<?>, Predicate<ItemStack>> disguises() {
        Map<EntityType<?>, Predicate<ItemStack>> cached = disguises;
        if (cached != null) {
            return cached;
        }
        Map<EntityType<?>, Predicate<ItemStack>> map = new IdentityHashMap<>();
        for (String mob : new String[]{"dicer", "frowzy", "skyvern", "tart", "whizz"}) {
            EntityType<?> type = entity(mob);
            ResourceLocation headId = new ResourceLocation("opposing_force", mob + "_head");
            Item head = ForgeRegistries.ITEMS.containsKey(headId) ? ForgeRegistries.ITEMS.getValue(headId) : null;
            if (type != null && head != null) {
                map.put(type, stack -> stack.is(head));
            }
        }
        EntityType<?> rambler = entity("rambler");
        if (rambler != null) {
            map.put(rambler, stack -> stack.is(RAMBLER_SKULLS));
        }
        disguises = map;
        return map;
    }

    private static EntityType<?> entity(String path) {
        ResourceLocation id = new ResourceLocation("opposing_force", path);
        return ForgeRegistries.ENTITY_TYPES.containsKey(id) ? ForgeRegistries.ENTITY_TYPES.getValue(id) : null;
    }

    private static double compute(LivingEntity entity, Attribute attribute) {
        double total = 0.0D;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Collection<AttributeModifier> modifiers = stack.getAttributeModifiers(slot).get(attribute);
            if (modifiers.isEmpty()) {
                continue;
            }
            for (AttributeModifier modifier : modifiers) {
                total += modifier.getAmount();
            }
        }
        return total;
    }
}
