package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.RingEarlyAnswer;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConcentricRingsStructurePlacement.class)
public abstract class MixinRingEarlyPlacement {

    @Inject(method = "isPlacementChunk", at = @At("HEAD"), cancellable = true)
    private void coo$answerEarly(ChunkGeneratorStructureState state, int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!RingEarlyAnswer.enabled()) {
            return;
        }
        state.ensureStructuresGenerated();
        RingEarlyAnswer early = ((RingEarlyAnswer.Source) state).coo$ringEarly((ConcentricRingsStructurePlacement) (Object) this);
        if (early == null) {
            return;
        }
        if (early.done()) {
            early.audit(chunkX, chunkZ);
        } else {
            cir.setReturnValue(early.contains(chunkX, chunkZ));
        }
    }
}
