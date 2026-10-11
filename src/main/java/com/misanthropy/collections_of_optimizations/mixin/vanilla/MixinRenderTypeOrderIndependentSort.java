package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.OrderIndependentSort;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderType.class)
public abstract class MixinRenderTypeOrderIndependentSort implements OrderIndependentSort {

    @Override
    public boolean coo$orderIndependent() {
        return false;
    }

    @ModifyExpressionValue(
            method = "end",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderType;sortOnUpload:Z"),
            require = 0
    )
    private boolean coo$skipOrderIndependentSort(boolean sortOnUpload) {
        return sortOnUpload && !(CoOConfig.vanillaSkipOrderIndependentSort && this.coo$orderIndependent());
    }
}
