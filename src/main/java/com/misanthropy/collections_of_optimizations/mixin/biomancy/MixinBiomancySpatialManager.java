package com.misanthropy.collections_of_optimizations.mixin.biomancy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BiomancySpatialState;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.github.elenterius.biomancy.world.spatial.SpatialShapeManager", remap = false)
public abstract class MixinBiomancySpatialManager {

    @Inject(method = "getLevelKey", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$reuseLevelKey(ServerLevel level, CallbackInfoReturnable<String> cir) {
        if (!CoOConfig.biomancyCacheSpatialLevelKey || level == null) {
            return;
        }
        cir.setReturnValue(BiomancySpatialState.levelKey(level));
    }
}
