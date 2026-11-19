package com.misanthropy.collections_of_optimizations.core;

import com.github.alexthe666.citadel.client.event.EventGetFluidRenderType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventListener;

public final class CitadelFluidRenderGate {

    private static final ListenerList LISTENERS = LionfishEventProbe.listOf(EventGetFluidRenderType.class);
    private static volatile Object seen;
    private static volatile boolean lavaOnly;

    private CitadelFluidRenderGate() {
    }

    public static boolean needsEvent(FluidState state) {
        return !lavaOnly() || state.is(Fluids.LAVA) || state.is(Fluids.FLOWING_LAVA);
    }

    private static boolean lavaOnly() {
        IEventListener[] listeners = LionfishEventProbe.listeners(LISTENERS);
        if (listeners == null) {
            return false;
        }
        if (listeners != seen) {
            int real = 0;
            boolean onlyAlexsMobs = true;
            for (IEventListener listener : listeners) {
                if (listener instanceof EventPriority) {
                    continue;
                }
                real++;
                if (!String.valueOf(listener).contains("alexsmobs")) {
                    onlyAlexsMobs = false;
                }
            }
            lavaOnly = real <= 1 && onlyAlexsMobs;
            seen = listeners;
        }
        return lavaOnly;
    }
}
