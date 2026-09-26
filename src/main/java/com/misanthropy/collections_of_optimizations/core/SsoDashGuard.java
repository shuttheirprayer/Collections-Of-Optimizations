package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public final class SsoDashGuard {

    private static final String SCHEDULER = "net.jrdemiurge.simplyswordsoverhaul.scheduler.Scheduler";
    private static final String STARS_EDGE = "net.sweenus.simplyswords.item.custom.StarsEdgeSwordItem";
    private static final String WHISPERWIND = "net.sweenus.simplyswords.item.custom.WhisperwindSwordItem";

    private static final String DASHING = "SimplySwordsWhisperwindDashing";
    private static final String MOB_KILLED = "SimplySwordsWhisperwindMobKilled";

    private static final String[] DASH_ITEM_IDS = {"emberlash", "storms_edge", "whisperwind"};

    private static volatile boolean resolved;
    private static volatile List<?> tasks;
    private static volatile Map<?, ?> savedPositions;
    private static volatile Map<?, ?> dashHits;
    private static volatile Item[] dashItems;

    private SsoDashGuard() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SsoDashGuard::onLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(SsoDashGuard::onServerStopped);
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!CoOConfig.simplyswordsoverhaulRestoreDashStateOnLogout) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        resolve();

        CompoundTag data = player.getPersistentData();
        boolean dashing = data.getBoolean(DASHING);
        if (dashing) {
            data.remove(DASHING);
            data.remove(MOB_KILLED);
            Map<?, ?> hits = dashHits;
            if (hits != null) {
                try {
                    hits.remove(player.getUUID());
                } catch (RuntimeException ignored) {
                }
            }
        }

        if (!player.isNoGravity()) {
            return;
        }
        if (dashing || (hasPendingTasks() && recentlyDashed(player))) {
            player.setNoGravity(false);
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        if (!CoOConfig.simplyswordsoverhaulResetSchedulerOnServerStop) {
            return;
        }
        resolve();
        List<?> pending = tasks;
        if (pending != null) {
            try {
                synchronized (pending) {
                    pending.clear();
                }
            } catch (RuntimeException ignored) {
            }
        }
        clear(savedPositions);
        clear(dashHits);
    }

    private static void clear(Map<?, ?> map) {
        if (map == null) {
            return;
        }
        try {
            map.clear();
        } catch (RuntimeException ignored) {
        }
    }

    private static boolean hasPendingTasks() {
        List<?> pending = tasks;
        if (pending == null) {
            return true;
        }
        try {
            synchronized (pending) {
                return !pending.isEmpty();
            }
        } catch (RuntimeException exception) {
            return true;
        }
    }

    private static boolean recentlyDashed(ServerPlayer player) {
        Item[] items = dashItems;
        if (items == null) {
            return false;
        }
        ItemCooldowns cooldowns = player.getCooldowns();
        for (Item item : items) {
            if (cooldowns.isOnCooldown(item)) {
                return true;
            }
        }
        return false;
    }

    private static void resolve() {
        if (resolved) {
            return;
        }
        synchronized (SsoDashGuard.class) {
            if (resolved) {
                return;
            }
            Object value = staticField(SCHEDULER, "tasks");
            tasks = value instanceof List<?> list ? list : null;
            value = staticField(STARS_EDGE, "savedPositions");
            savedPositions = value instanceof Map<?, ?> map ? map : null;
            value = staticField(WHISPERWIND, "DASH_HIT_MAP");
            dashHits = value instanceof Map<?, ?> map ? map : null;

            Item[] items = new Item[DASH_ITEM_IDS.length];
            int count = 0;
            for (String id : DASH_ITEM_IDS) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplyswords", id));
                if (item != null && item != Items.AIR) {
                    items[count++] = item;
                }
            }
            Item[] trimmed = new Item[count];
            System.arraycopy(items, 0, trimmed, 0, count);
            dashItems = trimmed;
            resolved = true;
        }
    }

    private static Object staticField(String owner, String name) {
        try {
            Class<?> type = Class.forName(owner);
            Field match = null;
            for (Field field : type.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String fieldName = field.getName();
                if (fieldName.equals(name)) {
                    match = field;
                    break;
                }
                if (match == null && fieldName.endsWith(name)) {
                    match = field;
                }
            }
            if (match == null) {
                return null;
            }
            match.setAccessible(true);
            return match.get(null);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
