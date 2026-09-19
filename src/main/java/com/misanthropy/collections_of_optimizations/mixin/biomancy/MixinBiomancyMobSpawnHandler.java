package com.misanthropy.collections_of_optimizations.mixin.biomancy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BiomancySpatialState;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.github.elenterius.biomancy.event.MobSpawnHandler", remap = false)
public abstract class MixinBiomancyMobSpawnHandler {

    @Inject(method = "onCheckSpawn", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipEmptyShapeQuery(MobSpawnEvent.PositionCheck event, CallbackInfo ci) {
        if (!CoOConfig.biomancySkipEmptySpawnQuery || event == null) {
            return;
        }
        if (event.getLevel() instanceof ServerLevel level && BiomancySpatialState.hasNoShapes(level)) {
            ci.cancel();
        }
    }
}
