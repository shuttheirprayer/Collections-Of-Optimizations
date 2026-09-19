package com.misanthropy.collections_of_optimizations.core;

import net.blay09.mods.cookingforblockheads.api.capability.AbstractKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.DefaultKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IngredientPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CfbKitchenItemIndex {

    private static final byte UNKNOWN = 0;
    private static final byte PLAIN = 1;
    private static final byte FRIDGE = 2;

    private static final ClassValue<Byte> KIND = new ClassValue<>() {
        @Override
        protected Byte computeValue(Class<?> type) {
            try {
                Method method = type.getMethod("findSource", IngredientPredicate.class, int.class, List.class, boolean.class, boolean.class);
                Class<?> owner = method.getDeclaringClass();
                if (owner == AbstractKitchenItemProvider.class || owner == DefaultKitchenItemProvider.class) {
                    return PLAIN;
                }
                if (owner.getName().startsWith("net.blay09.mods.cookingforblockheads.tile.FridgeBlockEntity$")) {
                    return FRIDGE;
                }
            } catch (NoSuchMethodException ignored) {
            }
            return UNKNOWN;
        }
    };

    private static int depth;
    private static volatile Entry current;

    private CfbKitchenItemIndex() {
    }

    public static void enter() {
        depth++;
    }

    public static void exit() {
        if (--depth <= 0) {
            depth = 0;
            current = null;
        }
    }

    public static boolean isAbsent(List<IKitchenItemProvider> inventories, Item item) {
        if (depth <= 0) {
            return false;
        }
        Entry entry = current;
        if (entry == null || entry.inventories != inventories) {
            entry = new Entry(inventories, build(inventories));
            current = entry;
        }
        return entry.items != null && !entry.items.contains(item);
    }

    private static Set<Item> build(List<IKitchenItemProvider> inventories) {
        Set<Item> items = new HashSet<>();
        for (IKitchenItemProvider provider : inventories) {
            byte kind = KIND.get(provider.getClass());
            if (kind == UNKNOWN) {
                return null;
            }
            if (kind == FRIDGE) {
                items.add(Items.SNOWBALL);
                items.add(Items.ICE);
            }
            int slots = provider.getSlots();
            for (int i = 0; i < slots; i++) {
                ItemStack stack = provider.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    items.add(stack.getItem());
                }
            }
        }
        return items;
    }

    private static final class Entry {

        private final List<IKitchenItemProvider> inventories;
        private final Set<Item> items;

        private Entry(List<IKitchenItemProvider> inventories, Set<Item> items) {
            this.inventories = inventories;
            this.items = items;
        }
    }
}
