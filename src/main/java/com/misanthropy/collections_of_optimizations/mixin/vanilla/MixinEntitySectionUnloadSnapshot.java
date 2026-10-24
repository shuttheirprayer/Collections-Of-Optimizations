package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinEntitySectionUnloadSnapshot {

    @Shadow
    @Final
    private LongSet chunksToUnload;

    @Shadow
    @Final
    private Long2ObjectMap<Visibility> chunkVisibility;

    @Shadow
    private boolean processChunkUnload(long pos) {
        throw new AssertionError();
    }

    @Inject(method = "processUnloads", at = @At("HEAD"), cancellable = true)
    private void coo$unloadFromSnapshot(CallbackInfo ci) {
        if (!CoOConfig.vanillaSnapshotEntityUnloads) {
            return;
        }
        ci.cancel();
        LongSet pending = this.chunksToUnload;
        if (pending.isEmpty()) {
            return;
        }
        for (long pos : pending.toLongArray()) {
            if (!pending.contains(pos)) {
                continue;
            }
            if (this.chunkVisibility.get(pos) != Visibility.HIDDEN || this.processChunkUnload(pos)) {
                pending.remove(pos);
            }
        }
    }
}
