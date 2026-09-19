package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;

public final class FdBossesPresence {

    private static final String SPAWNER_CLASS =
            "com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity";
    private static final String KINETIC_FIELD_CLASS =
            "com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldEntity";

    private static final Class<?> SPAWNER = find(SPAWNER_CLASS);
    private static final Class<?> KINETIC_FIELD = find(KINETIC_FIELD_CLASS);

    private static boolean serverArmed;
    private static int serverSpawners;
    private static int serverFields;

    private static boolean clientArmed;
    private static int clientSpawners;
    private static int clientFields;

    private FdBossesPresence() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(FdBossesPresence::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(FdBossesPresence::onLevelUnload);
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        serverSpawners = 0;
        serverFields = 0;
        serverArmed = false;
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            clientSpawners = 0;
            clientFields = 0;
            clientArmed = false;
        }
    }

    public static void onTrackingStart(EntityAccess entity, boolean clientSide) {
        if (clientSide) {
            clientArmed = true;
            if (SPAWNER != null && SPAWNER.isInstance(entity)) {
                clientSpawners++;
            }
            if (KINETIC_FIELD != null && KINETIC_FIELD.isInstance(entity)) {
                clientFields++;
            }
        } else {
            serverArmed = true;
            if (SPAWNER != null && SPAWNER.isInstance(entity)) {
                serverSpawners++;
            }
            if (KINETIC_FIELD != null && KINETIC_FIELD.isInstance(entity)) {
                serverFields++;
            }
        }
    }

    public static void onTrackingEnd(EntityAccess entity, boolean clientSide) {
        if (clientSide) {
            if (SPAWNER != null && SPAWNER.isInstance(entity) && clientSpawners > 0) {
                clientSpawners--;
            }
            if (KINETIC_FIELD != null && KINETIC_FIELD.isInstance(entity) && clientFields > 0) {
                clientFields--;
            }
        } else {
            if (SPAWNER != null && SPAWNER.isInstance(entity) && serverSpawners > 0) {
                serverSpawners--;
            }
            if (KINETIC_FIELD != null && KINETIC_FIELD.isInstance(entity) && serverFields > 0) {
                serverFields--;
            }
        }
    }

    public static boolean noServerSpawners() {
        return SPAWNER != null && serverArmed && serverSpawners == 0;
    }

    public static boolean noClientSpawners() {
        return SPAWNER != null && clientArmed && clientSpawners == 0;
    }

    public static boolean noKineticFields(Level level) {
        if (KINETIC_FIELD == null || level == null) {
            return false;
        }
        if (level.isClientSide()) {
            return clientArmed && clientFields == 0;
        }
        return serverArmed && serverFields == 0;
    }

    private static Class<?> find(String name) {
        try {
            return Class.forName(name, false, FdBossesPresence.class.getClassLoader());
        } catch (Throwable ignored) {
            return null;
        }
    }
}
