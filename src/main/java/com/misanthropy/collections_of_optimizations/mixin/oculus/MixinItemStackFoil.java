package com.misanthropy.collections_of_optimizations.mixin.oculus;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class MixinItemStackFoil {

    @ModifyReturnValue(method = "hasFoil", at = @At("RETURN"), require = 0)
    private boolean coo$skipGlintInShadowPass(boolean foil) {
        return foil && !(CoOConfig.oculusSkipGlintInShadowPass && IrisState.shadowPass());
    }
}
