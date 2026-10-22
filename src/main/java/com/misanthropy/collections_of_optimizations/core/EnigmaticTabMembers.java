package com.misanthropy.collections_of_optimizations.core;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class EnigmaticTabMembers {

    private static Map<CreativeModeTab, List<ICreativeTabMember>> byTab;

    private EnigmaticTabMembers() {
    }

    public static List<ICreativeTabMember> of(CreativeModeTab tab) {
        Map<CreativeModeTab, List<ICreativeTabMember>> map = byTab;
        if (map == null) {
            map = new IdentityHashMap<>();
            for (Item item : ForgeRegistries.ITEMS) {
                if (item instanceof ICreativeTabMember member) {
                    CreativeModeTab memberTab = member.getCreativeTab();
                    if (memberTab != null) {
                        map.computeIfAbsent(memberTab, k -> new ArrayList<>()).add(member);
                    }
                }
            }
            byTab = map;
        }
        return map.getOrDefault(tab, List.of());
    }
}
