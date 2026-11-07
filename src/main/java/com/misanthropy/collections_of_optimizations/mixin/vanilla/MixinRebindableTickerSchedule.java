package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.ScheduledTicker;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$RebindableTickingBlockEntityWrapper")
public abstract class MixinRebindableTickerSchedule implements ScheduledTicker {

    @Unique
    private long coo$due;

    @Unique
    private long coo$chunkKey = Long.MIN_VALUE;

    @Unique
    private byte coo$spreadVerdict;

    @Override
    public byte coo$spreadVerdict() {
        return this.coo$spreadVerdict;
    }

    @Override
    public void coo$spreadVerdict(byte verdict) {
        this.coo$spreadVerdict = verdict;
    }

    @Override
    public long coo$chunkKey() {
        return this.coo$chunkKey;
    }

    @Override
    public void coo$chunkKey(long chunkKey) {
        this.coo$chunkKey = chunkKey;
    }

    @Override
    public long coo$due() {
        return this.coo$due;
    }

    @Override
    public void coo$due(long due) {
        this.coo$due = due;
    }

    @Inject(method = "rebind", at = @At("HEAD"))
    private void coo$resetDue(TickingBlockEntity ticker, CallbackInfo ci) {
        this.coo$due = 0L;
        this.coo$chunkKey = Long.MIN_VALUE;
        this.coo$spreadVerdict = 0;
    }
}
