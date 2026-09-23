package com.misanthropy.collections_of_optimizations.core;

import com.github.L_Ender.lionfishapi.client.event.EventGetFluidRenderType;
import net.minecraftforge.eventbus.ListenerList;

public final class LionfishFluidRenderGate {

    private static final ListenerList LISTENERS = LionfishEventProbe.listOf(EventGetFluidRenderType.class);

    private LionfishFluidRenderGate() {
    }

    public static boolean hasListeners() {
        return LionfishEventProbe.hasListeners(LISTENERS);
    }
}
