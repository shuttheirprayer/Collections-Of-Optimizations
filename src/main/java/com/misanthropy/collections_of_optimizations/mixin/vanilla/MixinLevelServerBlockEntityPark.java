package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ScheduledTicker;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(Level.class)
public abstract class MixinLevelServerBlockEntityPark {

    @Shadow
    @Final
    public boolean isClientSide;

    @Shadow
    @Final
    protected List<TickingBlockEntity> blockEntityTickers;

    @Shadow
    @Final
    private List<TickingBlockEntity> pendingBlockEntityTickers;

    @Shadow
    private boolean tickingBlockEntities;

    @Shadow(remap = false)
    @Final
    private ArrayList<BlockEntity> freshBlockEntities;

    @Shadow(remap = false)
    @Final
    private ArrayList<BlockEntity> pendingFreshBlockEntities;

    @Shadow
    public abstract ProfilerFiller getProfiler();

    @Shadow
    public abstract boolean shouldTickBlocksAt(long chunkPos);

    @Shadow
    public abstract boolean shouldTickBlocksAt(BlockPos pos);

    @Unique
    private Long2ObjectOpenHashMap<ArrayList<TickingBlockEntity>> coo$parked;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void coo$parkDormantBlockEntities(CallbackInfo ci) {
        if (this.isClientSide) {
            return;
        }
        if (!CoOConfig.vanillaParkServerBlockEntities) {
            if (this.coo$parked != null && !this.coo$parked.isEmpty()) {
                this.coo$unparkAll();
            }
            return;
        }
        ci.cancel();
        ProfilerFiller profiler = this.getProfiler();
        profiler.push("blockEntities");
        if (!this.pendingFreshBlockEntities.isEmpty()) {
            this.freshBlockEntities.addAll(this.pendingFreshBlockEntities);
            this.pendingFreshBlockEntities.clear();
        }
        this.tickingBlockEntities = true;
        if (!this.freshBlockEntities.isEmpty()) {
            this.freshBlockEntities.forEach(BlockEntity::onLoad);
            this.freshBlockEntities.clear();
        }
        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }
        if (this.coo$parked != null && !this.coo$parked.isEmpty()) {
            this.coo$unparkTicking();
        } else if (this.coo$parked == null) {
            this.coo$parked = new Long2ObjectOpenHashMap<>();
        }
        List<TickingBlockEntity> tickers = this.blockEntityTickers;
        int size = tickers.size();
        int kept = 0;
        int index = 0;
        long verdictChunk = Long.MIN_VALUE;
        boolean verdict = false;
        try {
            for (; index < size; index++) {
                TickingBlockEntity ticker = tickers.get(index);
                if (ticker.isRemoved()) {
                    continue;
                }
                ScheduledTicker scheduled = ticker instanceof ScheduledTicker cast ? cast : null;
                if (scheduled == null) {
                    if (this.shouldTickBlocksAt(ticker.getPos())) {
                        ticker.tick();
                    }
                } else {
                    long chunk = scheduled.coo$chunkKey();
                    if (chunk == Long.MIN_VALUE) {
                        BlockPos pos = ticker.getPos();
                        if (pos != null) {
                            chunk = ChunkPos.asLong(pos);
                            scheduled.coo$chunkKey(chunk);
                        }
                    }
                    if (chunk == Long.MIN_VALUE) {
                        tickers.set(kept++, ticker);
                        continue;
                    }
                    if (chunk != verdictChunk) {
                        verdictChunk = chunk;
                        verdict = this.shouldTickBlocksAt(chunk);
                    }
                    if (!verdict) {
                        ArrayList<TickingBlockEntity> parked = this.coo$parked.get(chunk);
                        if (parked == null) {
                            parked = new ArrayList<>(4);
                            this.coo$parked.put(chunk, parked);
                        }
                        parked.add(ticker);
                        continue;
                    }
                    ticker.tick();
                }
                tickers.set(kept++, ticker);
            }
        } finally {
            if (index < size) {
                for (; index < size; index++) {
                    tickers.set(kept++, tickers.get(index));
                }
            }
            if (kept < size) {
                tickers.subList(kept, size).clear();
            }
            this.tickingBlockEntities = false;
            profiler.pop();
        }
    }

    @Unique
    private void coo$unparkTicking() {
        Iterator<Long2ObjectMap.Entry<ArrayList<TickingBlockEntity>>> it = this.coo$parked.long2ObjectEntrySet().fastIterator();
        while (it.hasNext()) {
            Long2ObjectMap.Entry<ArrayList<TickingBlockEntity>> entry = it.next();
            ArrayList<TickingBlockEntity> list = entry.getValue();
            if (list.get(0).isRemoved()) {
                list.removeIf(TickingBlockEntity::isRemoved);
                if (list.isEmpty()) {
                    it.remove();
                    continue;
                }
            }
            if (this.shouldTickBlocksAt(entry.getLongKey())) {
                this.blockEntityTickers.addAll(list);
                it.remove();
            }
        }
    }

    @Unique
    private void coo$unparkAll() {
        for (ArrayList<TickingBlockEntity> list : this.coo$parked.values()) {
            this.blockEntityTickers.addAll(list);
        }
        this.coo$parked.clear();
    }
}
