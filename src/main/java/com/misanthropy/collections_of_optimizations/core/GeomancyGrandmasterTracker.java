package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;

public final class GeomancyGrandmasterTracker {

    private static final String GRANDMASTER_CLASS =
            "com.gametechbc.gtbcs_geomancy_plus.entity.mobs.geo_grandmaster.GeoGrandmasterEntity";

    private static Class<?> grandmaster;
    private static boolean resolved;
    private static boolean armed;
    private static int loaded;

    private GeomancyGrandmasterTracker() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(GeomancyGrandmasterTracker::onServerStopped);
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        loaded = 0;
        armed = false;
    }

    public static void onTrackingStart(EntityAccess entity) {
        armed = true;
        Class<?> type = grandmasterClass();
        if (type != null && type.isInstance(entity)) {
            loaded++;
        }
    }

    public static void onTrackingEnd(EntityAccess entity) {
        Class<?> type = grandmasterClass();
        if (type != null && type.isInstance(entity)) {
            loaded--;
        }
    }

    public static boolean noneLoaded() {
        return armed && loaded <= 0 && grandmasterClass() != null;
    }

    private static Class<?> grandmasterClass() {
        if (!resolved) {
            resolved = true;
            try {
                grandmaster = Class.forName(GRANDMASTER_CLASS, false,
                        GeomancyGrandmasterTracker.class.getClassLoader());
            } catch (Throwable ignored) {
            }
        }
        return grandmaster;
    }
}
