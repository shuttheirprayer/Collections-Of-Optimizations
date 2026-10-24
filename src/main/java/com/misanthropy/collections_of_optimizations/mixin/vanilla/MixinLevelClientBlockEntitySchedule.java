package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ScheduledTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Level.class)
public abstract class MixinLevelClientBlockEntitySchedule {

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
    public abstract long getGameTime();

    @Shadow
    public abstract boolean shouldTickBlocksAt(BlockPos pos);

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void coo$scheduledClientBlockEntities(CallbackInfo ci) {
        if (!this.isClientSide || !CoOConfig.vanillaScheduleClientBlockEntities) {
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
        Level level = (Level) (Object) this;
        long now = this.getGameTime();
        List<TickingBlockEntity> tickers = this.blockEntityTickers;
        int size = tickers.size();
        int kept = 0;
        for (int index = 0; index < size; index++) {
            TickingBlockEntity ticker = tickers.get(index);
            ScheduledTicker scheduled = ticker instanceof ScheduledTicker cast ? cast : null;
            if (scheduled == null || scheduled.coo$due() <= now) {
                if (ticker.isRemoved()) {
                    continue;
                }
                BlockPos pos = ticker.getPos();
                if (pos != null && this.shouldTickBlocksAt(pos)) {
                    ticker.tick();
                } else if (scheduled != null && pos != null) {
                    scheduled.coo$due(ScheduledTicker.nextDue(level, ChunkPos.asLong(pos), now));
                }
            }
            if (kept != index) {
                tickers.set(kept, ticker);
            }
            kept++;
        }
        if (kept < size) {
            tickers.subList(kept, size).clear();
        }
        this.tickingBlockEntities = false;
        profiler.pop();
    }
}
