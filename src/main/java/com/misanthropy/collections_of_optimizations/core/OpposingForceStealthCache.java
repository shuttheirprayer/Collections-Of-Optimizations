package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public final class OpposingForceStealthCache {

    private static final ResourceLocation STEALTH_ID = new ResourceLocation("opposing_force", "stealth");

    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    private static Attribute stealthAttribute;

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
