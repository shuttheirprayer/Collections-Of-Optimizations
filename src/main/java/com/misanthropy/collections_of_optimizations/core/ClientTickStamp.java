package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class ClientTickStamp {

    private static int stamp;

    private ClientTickStamp() {
    }

    public static void register() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(ClientTickStamp::onClientTick);
        }
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            stamp++;
        }
    }

    public static int current() {
        return stamp;
    }

    public static int currentOnRenderThread() {
        return Minecraft.getInstance().isSameThread() ? stamp : -1;
    }
}
