package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import net.minecraft.server.level.TickingTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TickingTracker.class)
public abstract class MixinTickingTrackerMemo {

    @Shadow
    @Final
    protected Long2ByteMap chunks;

    @Unique
    private final Thread coo$owner = Thread.currentThread();

    @Unique
    private long coo$firstKey = Long.MIN_VALUE;

    @Unique
    private byte coo$firstLevel;

    @Unique
    private long coo$secondKey = Long.MIN_VALUE;

    @Unique
    private byte coo$secondLevel;

    @Overwrite
    protected int getLevel(long chunkPos) {
        if (!CoOConfig.vanillaTickingLevelMemo || Thread.currentThread() != this.coo$owner) {
            return this.chunks.get(chunkPos);
        }
        if (chunkPos == this.coo$firstKey) {
            return this.coo$firstLevel;
        }
        if (chunkPos == this.coo$secondKey) {
            return this.coo$secondLevel;
        }
        byte level = this.chunks.get(chunkPos);
        this.coo$secondKey = this.coo$firstKey;
        this.coo$secondLevel = this.coo$firstLevel;
        this.coo$firstKey = chunkPos;
        this.coo$firstLevel = level;
        return level;
    }

    @Overwrite
    protected void setLevel(long chunkPos, int level) {
        this.coo$firstKey = Long.MIN_VALUE;
        this.coo$secondKey = Long.MIN_VALUE;
        if (level > 33) {
            this.chunks.remove(chunkPos);
        } else {
            this.chunks.put(chunkPos, (byte) level);
        }
    }
}
