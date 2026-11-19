package com.misanthropy.collections_of_optimizations.mixin.terrablender;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CachedNamespacedRule;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SurfaceSystem.class)
public abstract class MixinSurfaceSystemNamespacedRule {

    @Redirect(method = "buildSurface", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/SurfaceRules$SurfaceRule;tryApply(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState coo$cachedNamespaceRule(SurfaceRules.SurfaceRule rule, int x, int y, int z) {
        if (CoOConfig.terrablenderCacheNamespaceRule && rule instanceof CachedNamespacedRule cached) {
            return cached.coo$apply(x, y, z);
        }
        return rule.tryApply(x, y, z);
    }
}
