package com.misanthropy.collections_of_optimizations.core;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ProbeCheck {

    public static final boolean ON = Boolean.getBoolean("coo.verifyFastProbes");
    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final AtomicLong CHECKED = new AtomicLong();
    private static final AtomicLong DIFFERENT = new AtomicLong();
    private static final Map<String, AtomicLong> BY_SITE = new ConcurrentHashMap<>();
    private static final int REPORT_EVERY = 20_000;
    private static final int DETAILED = 20;

    private ProbeCheck() {}

    public static void compare(String where, BlockHitResult fast, BlockHitResult stock) {
        boolean same = fast.getType() == stock.getType()
                && fast.getLocation().equals(stock.getLocation())
                && fast.getDirection() == stock.getDirection()
                && fast.getBlockPos().equals(stock.getBlockPos())
                && fast.isInside() == stock.isInside();
        count(where, same, () -> describe(fast) + " vs stock " + describe(stock));
    }

    public static void compareObjects(String where, Object fast, Object stock) {
        count(where, Objects.equals(fast, stock), () -> fast + " vs stock " + stock);
    }

    public static void compare(String where, boolean fast, boolean stock) {
        count(where, fast == stock, () -> fast + " vs stock " + stock);
    }

    private static void count(String where, boolean same, java.util.function.Supplier<String> detail) {
        long checked = CHECKED.incrementAndGet();
        BY_SITE.computeIfAbsent(where, k -> new AtomicLong()).incrementAndGet();
        if (!same) {
            long different = DIFFERENT.incrementAndGet();
            if (different <= DETAILED) LOGGER.warn("[CoO probe check] {} differs: {}", where, detail.get());
        }
        if (checked % REPORT_EVERY == 0) {
            LOGGER.info("[CoO probe check] {} checked, {} different, by site {}", checked, DIFFERENT.get(), BY_SITE);
        }
    }

    private static String describe(BlockHitResult hit) {
        return Objects.toString(hit.getType()) + " " + hit.getLocation() + " " + hit.getDirection() + " " + hit.getBlockPos() + " inside=" + hit.isInside();
    }
}
