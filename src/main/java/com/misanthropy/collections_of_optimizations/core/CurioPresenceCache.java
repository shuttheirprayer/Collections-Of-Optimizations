package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Collections;
import java.util.Set;

public final class CurioPresenceCache {

    private CurioPresenceCache() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(CurioPresenceCache::onCurioChanged);
        MinecraftForge.EVENT_BUS.addListener(CurioPresenceCache::onCurioEquip);
        MinecraftForge.EVENT_BUS.addListener(CurioPresenceCache::onCurioUnequip);
    }

    private static void onCurioChanged(CurioChangeEvent event) {
        invalidate(event.getEntity());
    }

    private static void onCurioEquip(CurioEquipEvent event) {
        invalidate(event.getEntity());
    }

    private static void onCurioUnequip(CurioUnequipEvent event) {
        invalidate(event.getEntity());
    }

    private static void invalidate(LivingEntity entity) {
        if (entity instanceof CurioPresenceHolder holder) {
            holder.coo$invalidateCurioPresence();
        }
    }

    public static boolean mayHaveEquipped(LivingEntity entity, Item item) {
        if (item == null) {
            return true;
        }
        Set<Item> equipped = equippedItems(entity);
        return equipped == null || equipped.contains(item);
    }

    public static Boolean equippedInstanceOf(LivingEntity entity, Class<?> type) {
        if (type == null) {
            return null;
        }
        Set<Item> equipped = equippedItems(entity);
        if (equipped == null) {
            return null;
        }
        for (Item item : equipped) {
            if (type.isInstance(item)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static Set<Item> equippedItems(LivingEntity entity) {
        if (!(entity instanceof CurioPresenceHolder holder)) {
            return null;
        }

        long stamp = entity.tickCount;
        Set<Item> cached = holder.coo$curioPresence();
        if (cached != null && holder.coo$curioPresenceStamp() == stamp) {
            return cached;
        }

        Set<Item> built = build(entity);
        if (built != null) {
            holder.coo$storeCurioPresence(built, stamp);
        }
        return built;
    }

    private static Set<Item> build(LivingEntity entity) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return null;
        }

        Set<Item> items = null;
        try {
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                for (int i = 0, slots = stackHandler.getSlots(); i < slots; i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        if (items == null) {
                            items = new ReferenceOpenHashSet<>(8);
                        }
                        items.add(stack.getItem());
                    }
                }
            }
        } catch (RuntimeException exception) {
            return null;
        }
        return items == null ? Collections.emptySet() : items;
    }
}
