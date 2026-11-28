package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class RingEarlyAnswer {

    public interface Source {
        RingEarlyAnswer coo$ringEarly(ConcentricRingsStructurePlacement placement);
    }

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int REACH = 8;
    private static final int AUDITS = 64;
    private static volatile boolean broken;

    private final int[] guesses;
    private final List<CompletableFuture<ChunkPos>> positions;
    private final CompletableFuture<List<ChunkPos>> all;
    private final AtomicInteger audits = new AtomicInteger(AUDITS);

    private RingEarlyAnswer(int[] guesses, List<CompletableFuture<ChunkPos>> positions, CompletableFuture<List<ChunkPos>> all) {
        this.guesses = guesses;
        this.positions = positions;
        this.all = all;
    }

    public static boolean enabled() {
        return CoOConfig.vanillaRingEarlyAnswer && !broken;
    }

    public static RingEarlyAnswer create(ConcentricRingsStructurePlacement placement, long seed,
                                         List<CompletableFuture<ChunkPos>> positions, CompletableFuture<List<ChunkPos>> all) {
        int distance = placement.distance();
        int count = placement.count();
        int spread = placement.spread();
        if (positions.size() != count) {
            return null;
        }
        int[] guesses = new int[count * 2];
        RandomSource random = RandomSource.create();
        random.setSeed(seed);
        double angle = random.nextDouble() * Math.PI * 2.0;
        int inRing = 0;
        int ring = 0;
        for (int i = 0; i < count; i++) {
            double radius = 4 * distance + distance * ring * 6 + (random.nextDouble() - 0.5) * (distance * 2.5);
            guesses[i * 2] = (int) Math.round(Math.cos(angle) * radius);
            guesses[i * 2 + 1] = (int) Math.round(Math.sin(angle) * radius);
            random.fork();
            angle += (Math.PI * 2) / spread;
            if (++inRing == spread) {
                ring++;
                inRing = 0;
                spread += 2 * spread / (ring + 1);
                spread = Math.min(spread, count - i);
                angle += random.nextDouble() * Math.PI * 2.0;
            }
        }
        RingEarlyAnswer early = new RingEarlyAnswer(guesses, positions, all);
        all.whenComplete((list, failure) -> {
            if (list != null) {
                early.checkReach(list);
            }
        });
        return early;
    }

    public boolean done() {
        return this.all.isDone();
    }

    public boolean contains(int chunkX, int chunkZ) {
        int[] guesses = this.guesses;
        for (int i = 0; i < guesses.length; i += 2) {
            if (Math.abs(guesses[i] - chunkX) <= REACH && Math.abs(guesses[i + 1] - chunkZ) <= REACH) {
                ChunkPos pos = this.positions.get(i >> 1).join();
                if (pos.x == chunkX && pos.z == chunkZ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void audit(int chunkX, int chunkZ) {
        if (this.audits.get() <= 0 || this.audits.getAndDecrement() <= 0 || this.all.isCompletedExceptionally()) {
            return;
        }
        boolean expected = this.all.join().contains(new ChunkPos(chunkX, chunkZ));
        if (expected != this.contains(chunkX, chunkZ)) {
            fail("the early answer for chunk " + chunkX + "," + chunkZ + " differs from the full list");
        }
    }

    private void checkReach(List<ChunkPos> list) {
        for (int i = 0; i < list.size(); i++) {
            ChunkPos pos = list.get(i);
            if (Math.abs(guesses[i * 2] - pos.x) > REACH || Math.abs(guesses[i * 2 + 1] - pos.z) > REACH) {
                fail("ring position " + i + " is more than " + REACH + " chunks from its first guess");
                return;
            }
        }
    }

    private static void fail(String reason) {
        broken = true;
        LOGGER.error("Ring early answers switched off: {}", reason);
    }
}
