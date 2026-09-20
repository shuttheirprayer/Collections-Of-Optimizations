package com.misanthropy.collections_of_optimizations.mixin.corpse;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import de.maxhenkel.corpse.entities.CorpseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CorpseEntity.class, remap = false)
public abstract class MixinCorpseEntityIdleScan {

    @Unique
    private int coo$idleTicks;

    @Unique
    private boolean coo$skipScan;

    @Unique
    private boolean coo$emptyKnown;

    @Unique
    private boolean coo$emptyCached;

    @WrapWithCondition(
            method = "m_8119_()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/maxhenkel/corpse/entities/CorpseEntity;setIsSkeleton(Z)V"
            ),
            require = 0
    )
    private boolean coo$throttleSkeletonFlag(CorpseEntity self, boolean skeleton) {
        int interval = CoOConfig.corpseIdleScanInterval;
        if (interval <= 1) {
            this.coo$skipScan = false;
            return true;
        }
        int ticks = this.coo$idleTicks;
        this.coo$idleTicks = ticks + 1 >= interval ? 0 : ticks + 1;
        boolean due = ticks == 0;
        this.coo$skipScan = !due;
        return due;
    }

    @WrapOperation(
            method = "m_8119_()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/maxhenkel/corpse/entities/CorpseEntity;isEmpty()Z"
            ),
            require = 0
    )
    private boolean coo$throttleEmptyScan(CorpseEntity self, Operation<Boolean> original) {
        if (!this.coo$skipScan || !this.coo$emptyKnown) {
            this.coo$emptyCached = original.call(self);
            this.coo$emptyKnown = true;
        }
        return this.coo$emptyCached;
    }
}
