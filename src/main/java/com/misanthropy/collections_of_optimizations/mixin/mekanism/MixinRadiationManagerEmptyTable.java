package com.misanthropy.collections_of_optimizations.mixin.mekanism;

import com.google.common.collect.Table;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mekanism.common.lib.radiation.RadiationManager", remap = false)
public abstract class MixinRadiationManagerEmptyTable {

    @Unique
    private static Object coo$baselineResult;

    @Shadow
    @Final
    private Table<?, ?, ?> radiationTable;

    @Inject(
            method = "getRadiationLevel(Lnet/minecraft/world/entity/Entity;)D",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$levelOfEntity(Entity entity, CallbackInfoReturnable<Double> cir) {
        if (CoOConfig.mekanismSkipEmptyRadiationScan && entity != null && this.radiationTable.isEmpty()) {
            cir.setReturnValue(1.0E-7D);
        }
    }

    @Inject(
            method = "getRadiationLevelAndMaxMagnitude(Lnet/minecraft/world/entity/Entity;)Lmekanism/common/lib/radiation/RadiationManager$LevelAndMaxMagnitude;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$magnitudeOfEntity(Entity entity, CallbackInfoReturnable<Object> cir) {
        Object cached = coo$baselineResult;
        if (cached != null && CoOConfig.mekanismSkipEmptyRadiationScan && entity != null && this.radiationTable.isEmpty()) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getRadiationLevelAndMaxMagnitude(Lmekanism/api/Coord4D;)Lmekanism/common/lib/radiation/RadiationManager$LevelAndMaxMagnitude;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$magnitudeOfCoord(@Coerce Object coord, CallbackInfoReturnable<Object> cir) {
        Object cached = coo$baselineResult;
        if (cached != null && CoOConfig.mekanismSkipEmptyRadiationScan && coord != null && this.radiationTable.isEmpty()) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getRadiationLevelAndMaxMagnitude(Lmekanism/api/Coord4D;)Lmekanism/common/lib/radiation/RadiationManager$LevelAndMaxMagnitude;",
            at = @At("RETURN"),
            require = 0
    )
    private void coo$rememberBaseline(@Coerce Object coord, CallbackInfoReturnable<Object> cir) {
        if (coo$baselineResult == null && CoOConfig.mekanismSkipEmptyRadiationScan && this.radiationTable.isEmpty()) {
            coo$baselineResult = cir.getReturnValue();
        }
    }
}
