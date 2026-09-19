package com.misanthropy.collections_of_optimizations.mixin.celestialcore;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CelestialCoreTransformScan;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.xiaoyue.celestial_core.content.recipes.TransformationRecipe", remap = false)
public abstract class MixinCCTransformationRecipe {

    @Inject(
            method = "m_5818_(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$matchStatBlock(Container container, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.celestialcoreMatchStatBlockRecipe) {
            return;
        }
        if (CelestialCoreTransformScan.rejects(this)) {
            cir.setReturnValue(false);
        }
    }
}
