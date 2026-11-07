package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.BoundTickingBlockEntityAccessor;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.RebindableTickingWrapperAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;

import java.util.ArrayList;

public final class SpreadBlockEntityTicks {

    private static final byte UNKNOWN = 0;
    private static final byte SPREAD = 1;
    private static final byte KEEP = 2;

    private static final ArrayList<TickingBlockEntity> QUEUE = new ArrayList<>();
    private static int head;
    private static int perFrame;
    private static Level owner;

    private SpreadBlockEntityTicks() {
    }

    public static boolean defer(Level level, TickingBlockEntity ticker, ScheduledTicker scheduled) {
        if (!CoOConfig.vanillaSpreadEntityTicks || !CoOConfig.vanillaSpreadBlockEntityTicks || scheduled == null) {
            return false;
        }
        byte verdict = scheduled.coo$spreadVerdict();
        if (verdict == UNKNOWN) {
            verdict = rendererless(ticker) ? SPREAD : KEEP;
            scheduled.coo$spreadVerdict(verdict);
        }
        if (verdict != SPREAD) {
            return false;
        }
        if (owner != level) {
            flush();
            owner = level;
        }
        QUEUE.add(ticker);
        perFrame = 0;
        return true;
    }

    private static boolean rendererless(TickingBlockEntity ticker) {
        if (!(ticker instanceof RebindableTickingWrapperAccessor wrapper)) {
            return false;
        }
        TickingBlockEntity inner = wrapper.coo$ticker();
        if (!(inner instanceof BoundTickingBlockEntityAccessor bound)) {
            return false;
        }
        BlockEntity blockEntity = bound.coo$blockEntity();
        return blockEntity != null && Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) == null;
    }

    public static void drainFrame(Level level) {
        if (head >= QUEUE.size()) {
            return;
        }
        if (level != owner) {
            discard();
            return;
        }
        if (perFrame == 0) {
            int frames = SpreadEntityTicks.framesPerSpread();
            perFrame = Math.max(1, (QUEUE.size() - head + frames - 1) / frames);
        }
        run(Math.min(QUEUE.size(), head + perFrame));
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
        try {
            while (head < end) {
                TickingBlockEntity ticker = QUEUE.get(head);
                QUEUE.set(head, null);
                head++;
                if (!ticker.isRemoved()) {
                    ticker.tick();
                }
            }
        } finally {
            if (head >= QUEUE.size()) {
                discard();
            }
        }
    }

    private static void discard() {
        QUEUE.clear();
        head = 0;
        perFrame = 0;
    }
}
