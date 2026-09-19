package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ImmersiveArmorsOverlayGate {

    private static volatile Item steampunkChestplate;

    private ImmersiveArmorsOverlayGate() {
    }

    public static boolean mayRender() {
        Item target = resolve();
        if (target == null) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return true;
        }
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || minecraft.options == null || minecraft.options.hideGui) {
            return false;
        }
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() == target) {
                return true;
            }
        }
        return false;
    }

    private static Item resolve() {
        Item cached = steampunkChestplate;
        if (cached != null) {
            return cached;
        }
        try {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation("immersive_armors", "steampunk_chestplate"));
            if (item != null && item != Items.AIR) {
                steampunkChestplate = item;
                return item;
            }
        } catch (Throwable throwable) {
            return null;
        }
        return null;
    }
}
