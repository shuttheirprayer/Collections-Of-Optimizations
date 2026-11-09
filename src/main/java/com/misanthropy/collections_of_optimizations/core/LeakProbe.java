package com.misanthropy.collections_of_optimizations.core;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class LeakProbe {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int MAX_DEPTH = 3;
    private static final int MAX_NODES_PER_ROOT = 10_000;
    private static final int MAX_ELEMENTS = 1_000;

    private LeakProbe() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(LeakProbe::onRegisterCommands);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("cooleakprobe")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    List<String> findings = run(context.getSource().getServer());
                    for (String finding : findings) {
                        context.getSource().sendSuccess(() -> Component.literal(finding), false);
                    }
                    return findings.size();
                }));
    }

    public static List<String> run(MinecraftServer server) {
        List<String> findings = new ArrayList<>();
        probeEventBus(server, findings);
        probeEntityRefs(server, findings);
        probeConnections(server, findings);
        probeModStatics(server, findings);

        if (findings.isEmpty()) {
            findings.add("No stale player holders found in the probed spots! "
                    + "(event bus listeners, mob aggro stuff, connections or even MCreator statics). "
                    + "The holder is somewhere these probes cannot see. You probably should do heapdump for further analyze");
        }
        for (String finding : findings) {
            LOGGER.warn("[LeakProbe] {}", finding);
        }
        return findings;
    }

    private static boolean isStale(MinecraftServer server, Object value) {
        return value instanceof ServerPlayer player
                && (player.hasDisconnected() || server.getPlayerList().getPlayer(player.getUUID()) != player);
    }

    private static String describe(ServerPlayer player) {
        return player.getGameProfile().getName() + " (" + (player.hasDisconnected() ? "logged out" : "replaced copy") + ")";
    }

    private static void probeEventBus(MinecraftServer server, List<String> findings) {
        Map<?, ?> listeners;
        try {
            Field field = EventBus.class.getDeclaredField("listeners");
            field.setAccessible(true);
            listeners = (Map<?, ?>) field.get(MinecraftForge.EVENT_BUS);
        } catch (Throwable t) {
            findings.add("Event bus probe unavailable (" + t.getClass().getSimpleName() + ") — skipped.");
            return;
        }

        for (Object listener : listeners.keySet()) {
            if (listener instanceof Class<?>) {
                continue;
            }
            scan(server, listener, "event bus listener " + listener.getClass().getName(), findings);
        }
    }

    private static void probeEntityRefs(MinecraftServer server, List<String> findings) {
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Mob mob && isStale(server, mob.getTarget())) {
                    findings.add("Mob target: " + where(entity) + " targets " + describe((ServerPlayer) mob.getTarget()));
                }
                if (entity instanceof LivingEntity living) {
                    if (isStale(server, living.getLastHurtByMob())) {
                        findings.add("Aggro ref: " + where(entity) + " lastHurtByMob is "
                                + describe((ServerPlayer) living.getLastHurtByMob()));
                    }
                    if (isStale(server, living.getLastHurtMob())) {
                        findings.add("Aggro ref: " + where(entity) + " lastHurtMob is "
                                + describe((ServerPlayer) living.getLastHurtMob()));
                    }
                }
            }
        }
    }

    private static String where(Entity entity) {
        return entity.getType().builtInRegistryHolder().key().location() + " at "
                + entity.getBlockX() + " " + entity.getBlockY() + " " + entity.getBlockZ()
                + " in " + entity.level().dimension().location();
    }

    private static void probeConnections(MinecraftServer server, List<String> findings) {
        if (server.getConnection() == null) {
            return;
        }
        for (Connection connection : server.getConnection().getConnections()) {
            if (connection.getPacketListener() instanceof ServerGamePacketListenerImpl game
                    && isStale(server, game.player)) {
                findings.add("Connection list still holds a connection for " + describe(game.player)
                        + " (connected=" + connection.isConnected() + ")");
            }
        }
    }

    private static void probeModStatics(MinecraftServer server, List<String> findings) {
        ModList.get().forEachModContainer((modId, container) -> {
            Object mod = container.getMod();
            if (mod == null || !mod.getClass().getName().startsWith("net.mcreator")) {
                return;
            }
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()) {
                    continue;
                }
                Object value;
                try {
                    field.setAccessible(true);
                    value = field.get(null);
                } catch (Throwable t) {
                    continue;
                }
                if (value != null) {
                    scan(server, value, "mod " + modId + " static " + field.getName(), findings);
                }
            }
        });
    }

    private static void scan(MinecraftServer server, Object root, String rootName, List<String> findings) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        scan(server, root, rootName, findings, visited, MAX_DEPTH);
    }

    private static void scan(MinecraftServer server, Object value, String path, List<String> findings,
                             Set<Object> visited, int depth) {
        if (value == null || visited.size() > MAX_NODES_PER_ROOT || !visited.add(value)) {
            return;
        }
        if (isStale(server, value)) {
            findings.add(path + " holds " + describe((ServerPlayer) value));
            return;
        }
        if (depth <= 0) {
            return;
        }

        if (value instanceof Collection<?> collection) {
            int i = 0;
            for (Object element : collection) {
                if (i++ >= MAX_ELEMENTS) {
                    break;
                }
                scan(server, element, path + "[element]", findings, visited, depth - 1);
            }
            return;
        }
        if (value instanceof Map<?, ?> map) {
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (i++ >= MAX_ELEMENTS) {
                    break;
                }
                scan(server, entry.getKey(), path + "[key]", findings, visited, depth - 1);
                scan(server, entry.getValue(), path + "[value]", findings, visited, depth - 1);
            }
            return;
        }
        if (value instanceof Object[] array) {
            int limit = Math.min(array.length, MAX_ELEMENTS);
            for (int i = 0; i < limit; i++) {
                scan(server, array[i], path + "[array]", findings, visited, depth - 1);
            }
            return;
        }

        String typeName = value.getClass().getName();
        if (typeName.startsWith("java.") || typeName.startsWith("net.minecraft") || typeName.startsWith("com.mojang")) {
            return;
        }
        for (Class<?> type = value.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    scan(server, field.get(value), path + "." + field.getName(), findings, visited, depth - 1);
                } catch (Throwable ignored) {
                }
            }
        }
    }
}
