package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;

import java.util.ArrayList;
import java.util.function.Consumer;

public final class SpreadEntityTicks {

    public interface Pending {
        boolean coo$spreadPending();

        void coo$spreadPending(boolean pending);
    }

    private static final long SPREAD_WINDOW_NANOS = 25_000_000L;
    public static int maxFrames = 6;

    private static final ArrayList<Entity> QUEUE = new ArrayList<>();
    private static int head;
    private static int perFrame;
    private static ClientLevel owner;
    private static Consumer<Entity> ticker;
    private static boolean draining;
    private static long lastDrain;
    private static long frameNanos = 4_000_000L;

    private SpreadEntityTicks() {
    }

    public static boolean defer(ClientLevel level, Consumer<Entity> consumer, Entity entity) {
        if (draining || !CoOConfig.vanillaSpreadEntityTicks || !spreadable(entity) || entity.isVehicle()) {
            return false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (entity == minecraft.getCameraEntity() || entity == minecraft.player) {
            return false;
        }
        if (owner != level) {
            flush();
            owner = level;
        }
        ticker = consumer;
        ((Pending) entity).coo$spreadPending(true);
        QUEUE.add(entity);
        return true;
    }

    private static boolean spreadable(Entity entity) {
        if (entity instanceof Mob mob) {
            return !mob.isLeashed();
        }
        return CoOConfig.vanillaSpreadMoreEntityTicks && (entity instanceof ItemEntity || entity instanceof ExperienceOrb
                || entity instanceof AbstractArrow || entity instanceof ThrowableItemProjectile);
    }

    public static void beginTick(ClientLevel level) {
        flush();
        owner = level;
    }

    public static void endTick() {
        int left = QUEUE.size() - head;
        if (left <= 0) {
            perFrame = 0;
            return;
        }
        int frames = framesPerSpread();
        perFrame = (left + frames - 1) / frames;
    }

    public static int framesPerSpread() {
        return (int) Math.max(1L, Math.min(maxFrames, SPREAD_WINDOW_NANOS / Math.max(1L, frameNanos)));
    }

    public static void drainFrame(ClientLevel level) {
        long now = System.nanoTime();
        if (lastDrain != 0L) {
            frameNanos += (now - lastDrain - frameNanos) / 8;
        }
        lastDrain = now;
        if (head >= QUEUE.size()) {
            return;
        }
        if (level != owner) {
            discard();
            return;
        }
        run(Math.min(QUEUE.size(), head + Math.max(1, perFrame)));
    }

    public static void flush() {
        if (head >= QUEUE.size()) {
            return;
        }
        if (owner != null && owner == Minecraft.getInstance().level) {
            run(QUEUE.size());
        } else {
            discard();
        }
    }

    private static void run(int end) {
        ClientLevel level = owner;
        Consumer<Entity> consumer = ticker;
        draining = true;
        try {
            while (head < end) {
                Entity entity = QUEUE.get(head);
                QUEUE.set(head, null);
                head++;
                ((Pending) entity).coo$spreadPending(false);
                if (!entity.isRemoved() && !entity.isPassenger() && entity.level() == level) {
                    level.guardEntityTick(consumer, entity);
                }
            }
        } finally {
            draining = false;
            if (head >= QUEUE.size()) {
                QUEUE.clear();
                head = 0;
            }
        }
    }

    private static void discard() {
        for (int index = head; index < QUEUE.size(); index++) {
            Entity entity = QUEUE.get(index);
            if (entity != null) {
                ((Pending) entity).coo$spreadPending(false);
            }
        }
        QUEUE.clear();
        head = 0;
    }

    public static float partialTick(Entity entity, float partialTick) {
        return ((Pending) entity).coo$spreadPending() ? 1.0F : partialTick;
    }
}
