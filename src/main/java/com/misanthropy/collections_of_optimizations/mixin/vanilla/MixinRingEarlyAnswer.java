package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.core.RingEarlyAnswer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkGeneratorStructureState.class)
public abstract class MixinRingEarlyAnswer implements RingEarlyAnswer.Source {

    @Shadow
    @Final
    private long concentricRingsSeed;

    @Unique
    private final Map<ConcentricRingsStructurePlacement, RingEarlyAnswer> coo$early = new ConcurrentHashMap<>();

    @Override
    public RingEarlyAnswer coo$ringEarly(ConcentricRingsStructurePlacement placement) {
        return this.coo$early.get(placement);
    }

    @WrapOperation(
            method = "generateRingPositions",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;sequence(Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;"),
            require = 0
    )
    private CompletableFuture<List<ChunkPos>> coo$captureRingPositions(List<CompletableFuture<ChunkPos>> positions,
                                                                        Operation<CompletableFuture<List<ChunkPos>>> original,
                                                                        @Local(argsOnly = true) ConcentricRingsStructurePlacement placement) {
        CompletableFuture<List<ChunkPos>> all = original.call(positions);
        if (RingEarlyAnswer.enabled()) {
            RingEarlyAnswer early = RingEarlyAnswer.create(placement, this.concentricRingsSeed, positions, all);
            if (early != null) {
                this.coo$early.put(placement, early);
            }
        }
        return all;
    }
}
