package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventExceptionHandler;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GuardedEvents {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final Object UNKNOWN = new Object();

    private static volatile boolean broken;
    private static boolean ready;
    private static IEventBus bus;
    private static int busId;
    private static boolean trackPhases;
    private static Field shutdownField;
    private static Field handlerField;
    private static Object pending;
    private static Method pendingGet;
    private static IEventExceptionHandler exceptionHandler;

    private static final Map<Class<?>, Plan> PLANS = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> DUMPED = new ConcurrentHashMap<>();

    private GuardedEvents() {
    }

    public static boolean post(IEventBus target, Event event) {
        if (broken || !CoOConfig.vanillaGuardedEvents || target != MinecraftForge.EVENT_BUS || !init(target)) {
            return target.post(event);
        }
        try {
            if (shutdownField.getBoolean(target)) {
                return false;
            }
        } catch (IllegalAccessException e) {
            broken = true;
            return target.post(event);
        }
        IEventListener[] all = event.getListenerList().getListeners(busId);
        Plan plan = PLANS.get(event.getClass());
        if (plan == null || plan.source != all) {
            plan = new Plan(all, event.getClass());
            PLANS.put(event.getClass(), plan);
        }
        int[] picked = plan.select(event);
        int i = 0;
        try {
            for (; i < picked.length; i++) {
                all[picked[i]].invoke(event);
            }
        } catch (Throwable t) {
            exceptionHandler.handleException(target, event, all, picked[i], t);
            throw t;
        }
        return event.isCanceled();
    }

    private static synchronized boolean init(IEventBus target) {
        if (ready) {
            return true;
        }
        if (broken) {
            return false;
        }
        try {
            Class<?> type = target.getClass();
            if (!type.getName().equals("net.minecraftforge.eventbus.EventBus")) {
                broken = true;
                return false;
            }
            Field id = type.getDeclaredField("busID");
            id.setAccessible(true);
            Field phases = type.getDeclaredField("trackPhases");
            phases.setAccessible(true);
            Field handler = type.getDeclaredField("exceptionHandler");
            handler.setAccessible(true);
            Field shutdown = type.getDeclaredField("shutdown");
            shutdown.setAccessible(true);
            Class<?> asm = Class.forName("net.minecraftforge.eventbus.ASMEventHandler", false, type.getClassLoader());
            Field wrapped = asm.getDeclaredField("handler");
            wrapped.setAccessible(true);
            Class<?> factory = Class.forName("net.minecraftforge.eventbus.ModLauncherFactory", false, type.getClassLoader());
            Field pendingField = factory.getDeclaredField("PENDING");
            pendingField.setAccessible(true);
            Object cache = pendingField.get(null);
            Method get = cache.getClass().getMethod("get", Object.class);
            get.setAccessible(true);
            busId = id.getInt(target);
            trackPhases = phases.getBoolean(target);
            exceptionHandler = (IEventExceptionHandler) handler.get(target);
            shutdownField = shutdown;
            handlerField = wrapped;
            pending = cache;
            pendingGet = get;
            bus = target;
            ready = true;
            return true;
        } catch (Throwable t) {
            broken = true;
            LOGGER.warn("Guarded event dispatch is off, the event bus did not look as expected", t);
            return false;
        }
    }

    private static Method methodOf(IEventListener listener) {
        try {
            if (!listener.getClass().getName().startsWith("net.minecraftforge.eventbus.ASMEventHandler")) {
                return null;
            }
            Object wrapper = handlerField.get(listener);
            Object method = pendingGet.invoke(pending, wrapper.getClass().getName());
            return method instanceof Method m ? m : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static final class Plan {
        final IEventListener[] source;
        final boolean[] marker;
        final List<ListenerGuards.Guard>[] guards;
        final Object[][] constants;
        final Class<?>[][] types;
        final ClassLoader[] loaders;
        final Map<Object, int[]> byStage = new ConcurrentHashMap<>();
        final ClassValue<Slot> byClass = new ClassValue<>() {
            @Override
            protected Slot computeValue(Class<?> type) {
                return new Slot();
            }
        };
        final Slot plain = new Slot();
        final int[] everything;
        final boolean filterable;

        @SuppressWarnings("unchecked")
        Plan(IEventListener[] source, Class<?> eventType) {
            this.source = source;
            int n = source.length;
            marker = new boolean[n];
            guards = new List[n];
            constants = new Object[n][];
            types = new Class<?>[n][];
            loaders = new ClassLoader[n];
            List<Integer> all = new ArrayList<>();
            boolean any = false;
            StringBuilder dump = new StringBuilder();
            for (int i = 0; i < n; i++) {
                IEventListener listener = source[i];
                if (listener instanceof EventPriority) {
                    marker[i] = true;
                    if (trackPhases) {
                        all.add(i);
                    }
                    continue;
                }
                all.add(i);
                Method method = methodOf(listener);
                List<ListenerGuards.Guard> found = method == null ? null : ListenerGuards.analyze(method);
                if (found != null) {
                    guards[i] = found;
                    loaders[i] = method.getDeclaringClass().getClassLoader();
                    constants[i] = new Object[found.size()];
                    types[i] = new Class<?>[found.size()];
                    for (int g = 0; g < found.size(); g++) {
                        ListenerGuards.Guard guard = found.get(g);
                        constants[i][g] = UNKNOWN;
                        try {
                            if (guard.owner() != null) {
                                Class<?> owner = Class.forName(guard.owner().replace('/', '.'), false, loaders[i]);
                                constants[i][g] = owner.getField(guard.name()).get(null);
                            } else if (guard.name() != null) {
                                types[i][g] = Class.forName(guard.name().replace('/', '.'), false, loaders[i]);
                            }
                        } catch (Throwable ignored) {
                            constants[i][g] = UNKNOWN;
                            types[i][g] = null;
                        }
                    }
                    any = true;
                }
                dump.append(found == null ? "  -    " : "  guard ").append(listener).append(found == null ? "" : " " + found).append('\n');
            }
            everything = all.stream().mapToInt(Integer::intValue).toArray();
            filterable = any;
            if (DUMPED.putIfAbsent(eventType.getName() + "#" + n, Boolean.TRUE) == null) {
                try {
                    Files.createDirectories(FMLPaths.GAMEDIR.get().resolve("logs"));
                    try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(FMLPaths.GAMEDIR.get().resolve("logs/coo-guarded-events.txt"),
                            StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND))) {
                        out.println("== " + eventType.getName() + " " + n);
                        out.print(dump);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        int[] select(Event event) {
            if (!filterable) {
                return everything;
            }
            Object stage = null;
            Object phase = null;
            Object side = null;
            Class<?> entity = null;
            Class<?> level = null;
            Class<?> entityLevel = null;
            int client = -1;
            Slot slot;
            Object keyA;
            Object keyB;
            if (event instanceof net.minecraftforge.client.event.RenderLevelStageEvent stageEvent) {
                stage = stageEvent.getStage();
                int[] cached = byStage.get(stage);
                if (cached != null) {
                    return cached;
                }
                slot = null;
                keyA = null;
                keyB = null;
            } else if (event instanceof TickEvent tick) {
                phase = tick.phase;
                side = tick.side;
                client = tick.side == LogicalSide.CLIENT ? 1 : 0;
                if (tick instanceof TickEvent.PlayerTickEvent playerTick) {
                    entity = playerTick.player.getClass();
                    Level l = playerTick.player.level();
                    entityLevel = l == null ? null : l.getClass();
                    slot = byClass.get(entity);
                    keyA = phase;
                    keyB = entityLevel;
                } else if (tick instanceof TickEvent.LevelTickEvent levelTick) {
                    level = levelTick.level.getClass();
                    slot = byClass.get(level);
                    keyA = phase;
                    keyB = side;
                } else {
                    slot = plain;
                    keyA = phase;
                    keyB = side;
                }
            } else if (event instanceof LivingEvent.LivingTickEvent living) {
                Entity e = living.getEntity();
                entity = e.getClass();
                Level l = e.level();
                client = l != null && l.isClientSide ? 1 : 0;
                entityLevel = l == null ? null : l.getClass();
                slot = byClass.get(entity);
                keyA = null;
                keyB = entityLevel;
            } else {
                return everything;
            }
            if (slot != null) {
                int[] cached = slot.find(keyA, keyB);
                if (cached != null) {
                    return cached;
                }
            }
            int[] out = new int[everything.length];
            int count = 0;
            for (int index : everything) {
                if (marker[index] || guards[index] == null || passes(index, stage, phase, side, entity, level, entityLevel, client)) {
                    out[count++] = index;
                }
            }
            int[] picked = java.util.Arrays.copyOf(out, count);
            if (slot == null) {
                if (byStage.size() < 4096) {
                    byStage.put(stage, picked);
                }
            } else {
                slot.add(keyA, keyB, picked);
            }
            return picked;
        }

        private boolean passes(int index, Object stage, Object phase, Object side, Class<?> entity, Class<?> level, Class<?> entityLevel, int client) {
            List<ListenerGuards.Guard> list = guards[index];
            for (int g = 0; g < list.size(); g++) {
                ListenerGuards.Guard guard = list.get(g);
                Object constant = constants[index][g];
                Class<?> type = types[index][g];
                int verdict = switch (guard.value()) {
                    case ListenerGuards.STAGE -> same(stage, constant, guard.test());
                    case ListenerGuards.PHASE -> same(phase, constant, guard.test());
                    case ListenerGuards.SIDE -> same(side, constant, guard.test());
                    case ListenerGuards.ENTITY -> instance(entity, type, guard.test());
                    case ListenerGuards.LEVEL -> instance(level, type, guard.test());
                    case ListenerGuards.ENTITY_LEVEL -> instance(entityLevel, type, guard.test());
                    case ListenerGuards.CLIENT_SIDE -> client < 0 ? -1 : (client == 1) == (guard.test() == ListenerGuards.IS_TRUE) ? 1 : 0;
                    default -> -1;
                };
                if (verdict == 0) {
                    return false;
                }
            }
            return true;
        }

        private static int same(Object actual, Object constant, int test) {
            if (actual == null || constant == UNKNOWN) {
                return -1;
            }
            boolean equal = actual == constant;
            return (test == ListenerGuards.EQUALS) == equal ? 1 : 0;
        }

        private static int instance(Class<?> actual, Class<?> type, int test) {
            if (actual == null || type == null) {
                return -1;
            }
            boolean is = type.isAssignableFrom(actual);
            return (test == ListenerGuards.IS_INSTANCE) == is ? 1 : 0;
        }
    }

    private static final class Slot {
        private static final Pick[] NONE = new Pick[0];

        private volatile Pick[] picks = NONE;

        int[] find(Object a, Object b) {
            Pick[] current = picks;
            for (int i = 0; i < current.length; i++) {
                Pick pick = current[i];
                if (pick.a == a && pick.b == b) {
                    return pick.picked;
                }
            }
            return null;
        }

        synchronized void add(Object a, Object b, int[] picked) {
            Pick[] current = picks;
            if (current.length >= 64) {
                return;
            }
            for (Pick pick : current) {
                if (pick.a == a && pick.b == b) {
                    return;
                }
            }
            Pick[] grown = java.util.Arrays.copyOf(current, current.length + 1);
            grown[current.length] = new Pick(a, b, picked);
            picks = grown;
        }
    }

    private record Pick(Object a, Object b, int[] picked) {
    }
}
