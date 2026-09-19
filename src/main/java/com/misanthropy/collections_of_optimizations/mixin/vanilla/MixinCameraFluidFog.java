package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCameraFluidFog {

    @Unique
    private FogType coo$fluidInCamera;

    @Inject(
            method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("RETURN"),
            require = 0
    )
    private void coo$dropFluidMemo(Vec3 position, CallbackInfo ci) {
        this.coo$fluidInCamera = null;
    }

    @WrapMethod(method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", require = 0)
    private FogType coo$memoFluid(Operation<FogType> original) {
        if (!CoOConfig.vanillaMemoCameraFluid) {
            return original.call();
        }
        FogType memo = this.coo$fluidInCamera;
        if (memo != null) {
            return memo;
        }
        FogType value = original.call();
        this.coo$fluidInCamera = value;
        return value;
    }
}
