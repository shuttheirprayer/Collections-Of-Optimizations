package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StalePlayerMaps {

    private static final int SWEEP_INTERVAL = 200;

    private static final String[][] SERVER_OWNERS = {
            {"moreartifacts", "net.gobies.moreartifacts.util.MAUtils"},
            {"moreartifacts", "net.gobies.moreartifacts.util.Teleport"},
            {"moreartifacts", "net.gobies.moreartifacts.item.artifacts.NecroplasmAmuletItem"},
            {"moreartifacts", "net.gobies.moreartifacts.item.artifacts.SpectreAmuletItem"},
            {"tonsofenchants", "com.deltajay.tonsofenchants.enchantments.Enchantments.Excavator"}
    };

    private static final String[][] CLIENT_OWNERS = {
            {"celestisynth", "org.thecelestialworkshop.celestisynth.api.animation.player.CSAnimator"},
            {"brutality", "net.goo.brutality.client.player_animation.AnimationHelper"}
    };

    private static List<Map<?, ?>> serverMaps;
    private static List<Map<?, ?>> clientMaps;
    private static int serverTicks;
    private static int clientTicks;

    private StalePlayerMaps() {
    }

    public static void register() {
        if (anyLoaded(SERVER_OWNERS)) {
            MinecraftForge.EVENT_BUS.addListener(StalePlayerMaps::onServerTick);
            MinecraftForge.EVENT_BUS.addListener(StalePlayerMaps::onClone);
            MinecraftForge.EVENT_BUS.addListener(StalePlayerMaps::onLoggedOut);
        }
        if (FMLEnvironment.dist.isClient() && anyLoaded(CLIENT_OWNERS)) {
            Client.register();
        }
    }

    private static boolean anyLoaded(String[][] owners) {
        for (String[] owner : owners) {
            if (ModList.get().isLoaded(owner[0])) {
                return true;
            }
        }
        return false;
    }

    private static List<Map<?, ?>> serverMaps() {
        if (serverMaps == null) {
            serverMaps = resolve(SERVER_OWNERS);
        }
        return serverMaps;
    }

    private static List<Map<?, ?>> clientMaps() {
        if (clientMaps == null) {
            clientMaps = resolve(CLIENT_OWNERS);
        }
        return clientMaps;
    }

    private static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ++serverTicks >= SWEEP_INTERVAL) {
            serverTicks = 0;
            sweep(serverMaps(), null);
        }
    }

    private static void onClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide()) {
            sweep(serverMaps(), event.getOriginal());
        }
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            sweep(serverMaps(), event.getEntity());
        }
    }

    private static void sweep(List<Map<?, ?>> maps, Entity gone) {
        if (!CoOConfig.vanillaPurgeStalePlayerMaps || maps.isEmpty()) {
            return;
        }
        for (Map<?, ?> map : maps) {
            if (map.isEmpty()) {
                continue;
            }
            try {
                if (gone != null) {
                    map.remove(gone);
                }
                map.keySet().removeIf(key -> key instanceof Entity entity && entity.isRemoved());
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static List<Map<?, ?>> resolve(String[][] owners) {
        List<Map<?, ?>> maps = new ArrayList<>();
        for (String[] owner : owners) {
            if (!ModList.get().isLoaded(owner[0])) {
                continue;
            }
            try {
                for (Field field : Class.forName(owner[1], false, StalePlayerMaps.class.getClassLoader()).getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers()) || !Map.class.isAssignableFrom(field.getType())) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (field.get(null) instanceof Map<?, ?> map) {
                        maps.add(map);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return maps;
    }

    private static final class Client {

        static void register() {
            MinecraftForge.EVENT_BUS.addListener(Client::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(Client::onClone);
            MinecraftForge.EVENT_BUS.addListener(Client::onLoggingOut);
        }

        private static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END && ++clientTicks >= SWEEP_INTERVAL) {
                clientTicks = 0;
                sweep(clientMaps(), null);
            }
        }

        private static void onClone(ClientPlayerNetworkEvent.Clone event) {
            sweep(clientMaps(), event.getOldPlayer());
        }

        private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            if (!CoOConfig.vanillaPurgeStalePlayerMaps) {
                return;
            }
            for (Map<?, ?> map : clientMaps()) {
                try {
                    map.keySet().removeIf(key -> key instanceof Entity);
                } catch (RuntimeException ignored) {
                }
            }
        }
    }
}
