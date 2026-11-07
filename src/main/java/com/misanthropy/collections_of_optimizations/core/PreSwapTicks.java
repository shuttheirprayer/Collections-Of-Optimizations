package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class PreSwapTicks {

    public static boolean enabled = true;
    public static boolean ticked;

    private static final MethodHandle ASYNC_PARTICLES;
    private static final MethodHandle ASYNC_PRE_TICK;
    private static final MethodHandle ASYNC_POST_TICK;

    static {
        MethodHandle instance = null, pre = null, post = null;
        try {
            Class<?> behavior = Class.forName("forge.fun.qu_an.minecraft.asyncparticles.client.core.particle.tick.AsyncTickBehavior",
                    false, PreSwapTicks.class.getClassLoader());
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            instance = lookup.findStatic(behavior, "getInstance", MethodType.methodType(behavior))
                    .asType(MethodType.methodType(Object.class));
            pre = lookup.findVirtual(behavior, "preTick", MethodType.methodType(void.class, boolean.class))
                    .asType(MethodType.methodType(void.class, Object.class, boolean.class));
            post = lookup.findVirtual(behavior, "postTick", MethodType.methodType(void.class))
                    .asType(MethodType.methodType(void.class, Object.class));
        } catch (ReflectiveOperationException e) {
            instance = pre = post = null;
        }
        ASYNC_PARTICLES = instance;
        ASYNC_PRE_TICK = pre;
        ASYNC_POST_TICK = post;
    }

    private PreSwapTicks() {
    }

    public static boolean active() {
        return enabled && CoOConfig.vanillaPreSwapTicks;
    }

    public static void beforeTick(boolean last) {
        if (ASYNC_PRE_TICK == null) {
            return;
        }
        try {
            ASYNC_PRE_TICK.invokeExact((Object) ASYNC_PARTICLES.invokeExact(), last);
        } catch (Throwable t) {
            throw PreSwapTicks.<RuntimeException>sneaky(t);
        }
    }

    public static void afterTick() {
        if (ASYNC_POST_TICK == null) {
            return;
        }
        try {
            ASYNC_POST_TICK.invokeExact((Object) ASYNC_PARTICLES.invokeExact());
        } catch (Throwable t) {
            throw PreSwapTicks.<RuntimeException>sneaky(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneaky(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
