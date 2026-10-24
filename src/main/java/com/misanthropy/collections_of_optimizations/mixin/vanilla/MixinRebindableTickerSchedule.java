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
    }
}
