package com.misanthropy.collections_of_optimizations.core;

import com.github.alexthe668.domesticationinnovation.server.enchantment.DIEnchantmentRegistry;
import com.github.alexthe668.domesticationinnovation.server.entity.TameableUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class DomesticationHealers {
    private static final Map<Level, Set<LivingEntity>> HEALERS = new WeakHashMap<>();

    private DomesticationHealers() {
    }

    public static void track(Entity entity) {
        if (entity instanceof LivingEntity living && TameableUtils.hasEnchant(living, DIEnchantmentRegistry.HEALING_AURA)) {
            synchronized (HEALERS) {
                HEALERS.computeIfAbsent(living.level(), level -> Collections.newSetFromMap(new WeakHashMap<>())).add(living);
            }
        }
    }

    public static List<LivingEntity> nearby(LivingEntity hurt) {
        Level level = hurt.level();
        List<LivingEntity> candidates;
        synchronized (HEALERS) {
            Set<LivingEntity> known = HEALERS.get(level);
            if (known == null || known.isEmpty()) {
                return new ArrayList<>();
            }
            candidates = new ArrayList<>(known);
        }
        AABB box = hurt.getBoundingBox().inflate(16.0, 4.0, 16.0);
        List<LivingEntity> found = new ArrayList<>();
        for (LivingEntity animal : candidates) {
            if (animal.isRemoved() || animal.level() != level) {
                forget(level, animal);
                continue;
            }
            if (!TameableUtils.hasEnchant(animal, DIEnchantmentRegistry.HEALING_AURA)) {
                forget(level, animal);
                continue;
            }
            if (animal.getBoundingBox().intersects(box) && EntitySelector.NO_SPECTATORS.test(animal)
                    && TameableUtils.hasSameOwnerAs(animal, hurt) && TameableUtils.getHealingAuraTime(animal) == 0) {
                found.add(animal);
            }
        }
        return found;
    }

    private static void forget(Level level, LivingEntity animal) {
        synchronized (HEALERS) {
            Set<LivingEntity> known = HEALERS.get(level);
            if (known != null) {
                known.remove(animal);
            }
        }
    }
}
