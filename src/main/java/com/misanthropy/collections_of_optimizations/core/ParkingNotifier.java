package com.misanthropy.collections_of_optimizations.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public final class ParkingNotifier {

    private static final long MAX_PARK_NANOS = TimeUnit.MILLISECONDS.toNanos(50);

    private final AtomicInteger generation = new AtomicInteger();
    private final ConcurrentLinkedQueue<Thread> parked = new ConcurrentLinkedQueue<>();

    public void await() {
        int seen = generation.get();
        Thread self = Thread.currentThread();
        parked.add(self);
        if (generation.get() == seen) {
            LockSupport.parkNanos(this, MAX_PARK_NANOS);
        }
        parked.remove(self);
    }

    public void post() {
        generation.incrementAndGet();
        for (Thread thread : parked) {
            LockSupport.unpark(thread);
        }
    }

    public interface Holder {
        ParkingNotifier coo$parking();
    }
}
