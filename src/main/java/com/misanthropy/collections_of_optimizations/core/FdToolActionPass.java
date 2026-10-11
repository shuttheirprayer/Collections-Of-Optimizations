package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ToolAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class FdToolActionPass {

    private static final Object LOCK = new Object();

    private static int depth;

    private static volatile Map<ToolAction, List<Item>> matched;

    private FdToolActionPass() {
    }

    public static void enter() {
        synchronized (LOCK) {
            if (depth++ == 0) {
                matched = new ConcurrentHashMap<>();
            }
        }
    }

    public static void leave() {
        synchronized (LOCK) {
            if (--depth == 0) {
                matched = null;
            }
        }
    }

    public static Stream<? extends Ingredient.Value> values(ToolAction action, Stream<? extends Ingredient.Value> original) {
        Map<ToolAction, List<Item>> pass = matched;
        if (pass == null || action == null) {
            return original;
        }
        List<Item> items = pass.get(action);
        if (items == null) {
            List<? extends Ingredient.Value> values = original.toList();
            List<Item> found = new ArrayList<>(values.size());
            for (Ingredient.Value value : values) {
                if (!(value instanceof Ingredient.ItemValue)) {
                    return values.stream();
                }
                for (ItemStack stack : value.getItems()) {
                    found.add(stack.getItem());
                }
            }
            pass.putIfAbsent(action, List.copyOf(found));
            return values.stream();
        }
        List<Ingredient.Value> values = new ArrayList<>(items.size());
        for (Item item : items) {
            ItemStack stack = new ItemStack(item);
            if (stack.canPerformAction(action)) {
                values.add(new Ingredient.ItemValue(stack));
            }
        }
        return values.stream();
    }
}
