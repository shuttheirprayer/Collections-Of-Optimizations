package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;

public final class VehicleCount {

    private static Class<?> vehicleType;

    private VehicleCount() {
    }

    public interface Holder {
        int coo$vehicles();

        void coo$addVehicles(int delta);
    }

    public static void register() {
        if (!ModList.get().isLoaded("immersive_aircraft")) {
            return;
        }
        try {
            vehicleType = Class.forName("immersive_aircraft.entity.VehicleEntity");
        } catch (ClassNotFoundException | LinkageError e) {
            return;
        }
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, VehicleCount::onJoin);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityLeaveLevelEvent.class, VehicleCount::onLeave);
    }

    public static boolean tracking() {
        return vehicleType != null;
    }

    public static boolean none(Level level) {
        return vehicleType != null && level instanceof Holder holder && holder.coo$vehicles() <= 0;
    }

    private static void onJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (vehicleType.isInstance(entity) && event.getLevel() instanceof Holder holder) {
            holder.coo$addVehicles(1);
        }
    }

    private static void onLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (vehicleType.isInstance(entity) && event.getLevel() instanceof Holder holder) {
            holder.coo$addVehicles(-1);
        }
    }
}
