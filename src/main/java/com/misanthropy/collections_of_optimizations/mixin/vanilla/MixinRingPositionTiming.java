package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkGeneratorStructureState.class)
public abstract class MixinRingPositionTiming {

    @Unique
    private static final Logger COO_LOGGER = LogManager.getLogger("collections_of_optimizations");

    @Shadow
    @Final
    private Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions;

    @Shadow
    @Final
    private List<Holder<StructureSet>> possibleStructureSets;

    @Unique
    private long coo$ringStart;

    @Unique
    private final Map<ConcentricRingsStructurePlacement, String> coo$ringNames = new ConcurrentHashMap<>();

    @Inject(method = "generatePositions", at = @At("HEAD"))
    private void coo$markRingStart(CallbackInfo ci) {
        this.coo$ringStart = System.nanoTime();
    }

    @Inject(method = "generatePositions", at = @At("RETURN"))
    private void coo$logRingCompletion(CallbackInfo ci) {
        if (!CoOConfig.vanillaLogRingPositionTiming) {
            return;
        }
        long start = this.coo$ringStart;
        for (Map.Entry<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> entry : this.ringPositions.entrySet()) {
            ConcentricRingsStructurePlacement placement = entry.getKey();
            String name = "unknown";
            for (Holder<StructureSet> holder : this.possibleStructureSets) {
                if (holder.value().placement() == placement) {
                    name = holder.unwrapKey().map(key -> key.location().toString()).orElse("unknown");
                    break;
                }
            }
            String id = name;
            this.coo$ringNames.put(placement, id);
            entry.getValue().whenComplete((positions, failure) -> COO_LOGGER.info(
                    "Ring positions for {}: {} of {} ready {} ms after they were requested{}", id,
                    positions == null ? 0 : positions.size(), placement.count(), (System.nanoTime() - start) / 1_000_000L,
                    failure == null ? "" : " (failed: " + failure + ")"));
        }
    }

    @WrapOperation(
            method = "getRingPositionsFor",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;join()Ljava/lang/Object;"),
            require = 0
    )
    private Object coo$timeRingJoin(CompletableFuture<List<ChunkPos>> future, Operation<Object> original,
                                    @Local(argsOnly = true) ConcentricRingsStructurePlacement placement) {
        if (future.isDone() || !CoOConfig.vanillaLogRingPositionTiming) {
            return original.call(future);
        }
        long begin = System.nanoTime();
        try {
            return original.call(future);
        } finally {
            String id = this.coo$ringNames.remove(placement);
            if (id != null) {
                COO_LOGGER.info("Ring positions for {}: the first request waited {} ms on thread '{}'", id,
                        (System.nanoTime() - begin) / 1_000_000L, Thread.currentThread().getName());
            }
        }
    }
}
