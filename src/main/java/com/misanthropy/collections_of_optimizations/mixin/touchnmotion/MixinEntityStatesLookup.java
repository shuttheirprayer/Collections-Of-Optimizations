package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import strm.touchnmotion.interaction.EntityStates;

@Mixin(value = EntityStates.class, remap = false)
public abstract class MixinEntityStatesLookup<T> {

    @Unique
    private static final long coo$STALE_NANOS = 200_000_000L;
    @Unique
    private static final long coo$SWEEP_EVERY_NANOS = 1_000_000_000L;
    @Unique
    private static final int coo$SWEEP_ABOVE = 32;
    @Unique
    private static final int coo$MAX_ENTITIES = 256;
    @Unique
    private static int coo$removals;

    @Shadow
    @Final
    private Map<UUID, EntityStates.Entry<T>> entries;
    @Shadow
    @Final
    private Supplier<T> make;
    @Shadow
    private long sweptAt;

    @Unique
    private UUID coo$lastKey;
    @Unique
    private EntityStates.Entry<T> coo$lastEntry;
    @Unique
    private int coo$lastRemovals = -1;

    @Shadow
    private void sweep(long now) {}

    @Unique
    private EntityStates.Entry<T> coo$lookup(UUID uuid) {
        if (!CoOConfig.touchnmotionLastStateLookup) return entries.get(uuid);
        if (uuid == coo$lastKey && coo$lastRemovals == coo$removals) return coo$lastEntry;
        EntityStates.Entry<T> entry = entries.get(uuid);
        if (entry != null) {
            coo$lastKey = uuid;
            coo$lastEntry = entry;
            coo$lastRemovals = coo$removals;
        }
        return entry;
    }

    /**
     * @author Misanthropy
     * @reason opt
     */
    @Overwrite
    public EntityStates.Entry<T> seen(UUID uuid, long now) {
        int size = entries.size();
        if (size > coo$MAX_ENTITIES || size > coo$SWEEP_ABOVE && now - sweptAt > coo$SWEEP_EVERY_NANOS) {
            sweptAt = now;
            sweep(now);
        }
        EntityStates.Entry<T> entry = coo$lookup(uuid);
        if (entry == null) entry = entries.computeIfAbsent(uuid, k -> coo$newEntry(make.get()));
        ((EntityStatesEntryAccessor) (Object) entry).coo$setSeenAt(now);
        return entry;
    }

    /**
     * @author Misanthropy
     * @reason opt
     */
    @Overwrite
    public T fresh(UUID uuid) {
        EntityStates.Entry<T> entry = coo$lookup(uuid);
        if (entry == null || System.nanoTime() - ((EntityStatesEntryAccessor) (Object) entry).coo$seenAt() > coo$STALE_NANOS) return null;
        return entry.value;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static <V> EntityStates.Entry<V> coo$newEntry(V value) {
        return (EntityStates.Entry<V>) EntityStatesEntryAccessor.coo$create(value);
    }

    @Inject(method = {"sweep", "forget"}, at = @At("HEAD"))
    private void coo$forgetLastLookup(CallbackInfo ci) {
        coo$removals++;
    }

    @Inject(method = "clearAll", at = @At("HEAD"))
    private static void coo$forgetLastLookups(CallbackInfo ci) {
        coo$removals++;
    }
}
