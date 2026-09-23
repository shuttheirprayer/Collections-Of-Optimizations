package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public final class LionfishEventProbe {

    private static final int MAX_BUSES = 1024;
    private static final int BUS_ID = resolveBusId();

    private LionfishEventProbe() {
    }

    public static ListenerList listOf(Class<? extends Event> type) {
        try {
            return EventListenerHelper.getListenerList(type);
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static boolean hasListeners(ListenerList list) {
        if (list == null || BUS_ID < 0) {
            return true;
        }
        try {
            return list.getListeners(BUS_ID).length != 0;
        } catch (Throwable throwable) {
            return true;
        }
    }

    private static int resolveBusId() {
        ListenerList list = listOf(ProbeEvent.class);
        if (list == null) {
            return -1;
        }
        Consumer<ProbeEvent> probe = event -> {
        };
        try {
            int[] before = counts(list);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ProbeEvent.class, probe);
            int[] after = counts(list);
            int found = -1;
            for (int id = 0; id < after.length; id++) {
                int previous = id < before.length ? before[id] : 0;
                if (after[id] != previous) {
                    if (found != -1) {
                        return -1;
                    }
                    found = id;
                }
            }
            return found;
        } catch (Throwable throwable) {
            return -1;
        } finally {
            try {
                MinecraftForge.EVENT_BUS.unregister(probe);
            } catch (Throwable ignored) {
            }
        }
    }

    private static int[] counts(ListenerList list) {
        int[] scratch = new int[MAX_BUSES];
        int size = 0;
        try {
            while (size < MAX_BUSES) {
                scratch[size] = list.getListeners(size).length;
                size++;
            }
        } catch (IndexOutOfBoundsException expected) {
        }
        int[] result = new int[size];
        System.arraycopy(scratch, 0, result, 0, size);
        return result;
    }

    public static final class ProbeEvent extends Event {
    }
}
