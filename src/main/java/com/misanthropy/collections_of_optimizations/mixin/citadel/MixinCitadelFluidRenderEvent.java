package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CitadelFluidRenderGate;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlockRenderTypes.class, priority = 1500)
public abstract class MixinCitadelFluidRenderEvent {

    @TargetHandler(
            mixin = "com.github.alexthe666.citadel.mixin.client.ItemBlockRenderTypesMixin",
            name = "citadel_getFluidRenderLayer"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private static void coo$skipNonLavaFluidRenderEvent(FluidState fluidState, CallbackInfoReturnable<RenderType> cir, Operation<Void> original) {
        if (!CoOConfig.citadelSkipNonLavaFluidRenderEvent || CitadelFluidRenderGate.needsEvent(fluidState)) {
            original.call(fluidState, cir);
        }
    }
}
