package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.gobies.moreartifacts.init.MAItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MoreArtifactsEquipState {

    private static volatile Set<Item> tracked;
    private static volatile Item[] shields;
    private static volatile boolean failed;

    private MoreArtifactsEquipState() {
    }

    public static Map<Item, Boolean> of(Player player) {
        if (player == null || failed) {
            return null;
        }
        Set<Item> known = tracked;
        if (known == null) {
            if (!resolve()) {
                return null;
            }
            known = tracked;
        }
        Item[] hand = shields;
        if (known == null || hand == null) {
            return null;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(player);
        if (equipped == null) {
            return null;
        }
        Map<Item, Boolean> state = null;
        for (Item item : equipped) {
            if (known.contains(item)) {
                if (state == null) {
                    state = new HashMap<>();
                }
                state.put(item, Boolean.TRUE);
            }
        }
        Item main = player.getMainHandItem().getItem();
        Item off = player.getOffhandItem().getItem();
        for (Item shield : hand) {
            if (main == shield || off == shield) {
                if (state == null) {
                    state = new HashMap<>();
                }
                state.put(shield, Boolean.TRUE);
            }
        }
        return state == null ? Collections.emptyMap() : state;
    }

    private static synchronized boolean resolve() {
        if (tracked != null && shields != null) {
            return true;
        }
        if (failed) {
            return false;
        }
        try {
            Set<Item> items = new ReferenceOpenHashSet<>(64);
            for (RegistryObject<Item> holder : MAItems.getAllArtifacts()) {
                items.add(holder.get());
            }
            shields = new Item[]{MAItems.CobaltShield.get(), MAItems.ObsidianShield.get(), MAItems.AnkhShield.get()};
            tracked = items;
            return true;
        } catch (Throwable throwable) {
            failed = true;
            return false;
        }
    }
}
