package com.misanthropy.collections_of_optimizations.mixin.terrablender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TerrablenderStackedLayers;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrablender.api.SurfaceRuleManager;

@Mixin(value = SurfaceRuleManager.class, remap = false)
public abstract class MixinSurfaceRuleManagerLayers {

    @ModifyReturnValue(method = "getNamespacedRules", at = @At("RETURN"), require = 0)
    private static SurfaceRules.RuleSource coo$dropRepeatedLayers(SurfaceRules.RuleSource built) {
        return CoOConfig.terrablenderDedupeStackedLayers ? TerrablenderStackedLayers.dedupe(built) : built;
    }
}
