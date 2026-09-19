package com.misanthropy.collections_of_optimizations.core;

import alexthw.ars_elemental.client.ClientEvents;
import alexthw.ars_elemental.common.items.CurioHolder;
import alexthw.ars_elemental.network.NetworkManager;
import alexthw.ars_elemental.network.OpenCurioBagPacket;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.network.PacketDistributor;

public final class ArsElementalCurioBagKey {

    private static boolean coo$wasDown;

    private ArsElementalCurioBagKey() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ArsElementalCurioBagKey::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!CoOConfig.arselementalFixCurioBagKeybind) {
            coo$wasDown = false;
            return;
        }

        boolean down;
        try {
            down = ClientEvents.CURIO_BAG_KEYBINDING.isDown();
        } catch (RuntimeException exception) {
            coo$wasDown = false;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.screen != null) {
            coo$wasDown = down;
            return;
        }

        if (down && !coo$wasDown) {
            SlotReference bag = CurioHolder.isEquipped(player);
            if (bag != null && !bag.isEmpty()) {
                NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(), new OpenCurioBagPacket());
            }
        }
        coo$wasDown = down;
    }
}
