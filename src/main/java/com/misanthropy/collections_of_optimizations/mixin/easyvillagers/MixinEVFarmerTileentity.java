package com.misanthropy.collections_of_optimizations.mixin.easyvillagers;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import de.maxhenkel.easyvillagers.blocks.tileentity.FarmerTileentity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FarmerTileentity.class, remap = false)
public abstract class MixinEVFarmerTileentity {

    @Unique
    private int coo$dirtyTicks;

    @Unique
    private boolean coo$allowDirtyMark() {
        int interval = CoOConfig.easyvillagersDirtyMarkInterval;
        if (interval <= 1) {
            return true;
        }
        int ticks = this.coo$dirtyTicks;
        this.coo$dirtyTicks = ticks + 1 >= interval ? 0 : ticks + 1;
        return ticks == 0;
    }

    @WrapWithCondition(
            method = "tickServer()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/maxhenkel/easyvillagers/blocks/tileentity/FarmerTileentity;m_6596_()V",
                    ordinal = 0
            ),
            require = 0
    )
    private boolean coo$throttleIdleDirtyMark(FarmerTileentity self) {
        return coo$allowDirtyMark();
    }
}
