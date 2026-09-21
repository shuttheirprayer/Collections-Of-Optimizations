package com.misanthropy.collections_of_optimizations.mixin.refinedmod;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.refinedrain.refinedmod.entity.spells.CustomWaveEntity", remap = false)
public abstract class MixinRefinedWaveTerrain {

    @Unique
    private long coo$lastFireScanCell = Long.MIN_VALUE;

    @Unique
    private long coo$lastLavaScanCell = Long.MIN_VALUE;

    @Inject(
            method = "extinguishNearbyFires()V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$leanFireScan(CallbackInfo ci) {
        if (CoOConfig.refinedmodDisableWaveTerraforming) {
            ci.cancel();
            return;
        }
        Entity self = (Entity) (Object) this;
        if (CoOConfig.refinedmodSkipClientWaveTerrain && self.level().isClientSide) {
            ci.cancel();
            return;
        }
        if (CoOConfig.refinedmodLeanWaveTerrainScan) {
            long cell = coo$currentCell(self);
            if (cell == this.coo$lastFireScanCell) {
                ci.cancel();
                return;
            }
            this.coo$lastFireScanCell = cell;
        }
    }

    @Inject(
            method = "convertLavaToCobblestone()V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$leanLavaScan(CallbackInfo ci) {
        if (CoOConfig.refinedmodDisableWaveTerraforming) {
            ci.cancel();
            return;
        }
        Entity self = (Entity) (Object) this;
        if (CoOConfig.refinedmodSkipClientWaveTerrain && self.level().isClientSide) {
            ci.cancel();
            return;
        }
        if (CoOConfig.refinedmodLeanWaveTerrainScan) {
            long cell = coo$currentCell(self);
            if (cell == this.coo$lastLavaScanCell) {
                ci.cancel();
                return;
            }
            this.coo$lastLavaScanCell = cell;
        }
    }

    @Unique
    private static long coo$currentCell(Entity entity) {
        return BlockPos.asLong(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
    }
}
