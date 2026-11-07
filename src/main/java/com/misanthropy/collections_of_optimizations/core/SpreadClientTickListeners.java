package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventExceptionHandler;
import net.minecraftforge.eventbus.api.IEventListener;

import java.util.ArrayList;

public final class SpreadClientTickListeners {

    private record Entry(IEventBus bus, IEventExceptionHandler handler, Event event, IEventListener[] listeners, int[] picked) {
    }

    private static final ArrayList<Entry> QUEUE = new ArrayList<>();
    private static int head;
    private static int next;
    private static int pending;
    private static int perFrame;

    private SpreadClientTickListeners() {
    }

    static void queue(IEventBus target, IEventExceptionHandler exceptionHandler, Event posted, IEventListener[] all, int[] order) {
        if (order.length == 0) {
            return;
        }
        QUEUE.add(new Entry(target, exceptionHandler, posted, all, order));
        pending += order.length;
        int frames = SpreadEntityTicks.framesPerSpread();
        perFrame = (pending + frames - 1) / frames;
    }

    public static void drainFrame() {
        if (pending > 0) {
            run(Math.max(1, perFrame));
        }
    }

    public static void flush() {
        if (pending > 0) {
            run(Integer.MAX_VALUE);
        }
    }

    private static void run(int budget) {
        while (budget > 0 && head < QUEUE.size()) {
            Entry entry = QUEUE.get(head);
            if (next >= entry.picked.length) {
                QUEUE.set(head, null);
                head++;
                next = 0;
                continue;
            }
            int index = entry.picked[next++];
            pending--;
            budget--;
            try {
                entry.listeners[index].invoke(entry.event);
            } catch (Throwable t) {
                clear();
                entry.handler.handleException(entry.bus, entry.event, entry.listeners, index, t);
                throw t;
            }
        }
        while (head < QUEUE.size() && next >= QUEUE.get(head).picked.length) {
            QUEUE.set(head, null);
            head++;
            next = 0;
        }
        if (head >= QUEUE.size()) {
            clear();
        }
    }

    private static void clear() {
        QUEUE.clear();
        head = 0;
        next = 0;
        pending = 0;
    }
}
