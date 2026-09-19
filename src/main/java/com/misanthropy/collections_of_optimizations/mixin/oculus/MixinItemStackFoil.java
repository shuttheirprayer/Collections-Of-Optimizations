package com.misanthropy.collections_of_optimizations.mixin.oculus;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class MixinItemStackFoil {

    @WrapMethod(method = "hasFoil", require = 0)
    private boolean coo$skipGlintInShadowPass(Operation<Boolean> original) {
        return CoOConfig.oculusSkipGlintInShadowPass && IrisState.shadowPass() ? false : original.call();
    }
}
