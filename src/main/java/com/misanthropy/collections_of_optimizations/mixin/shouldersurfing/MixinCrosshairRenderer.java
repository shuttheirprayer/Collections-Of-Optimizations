package com.misanthropy.collections_of_optimizations.mixin.shouldersurfing;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CrosshairRenderer.class, remap = false)
public abstract class MixinCrosshairRenderer {

    @WrapMethod(method = "computeCrosshairOffset", require = 0)
    private static Vec2f coo$skipIdlePick(IShoulderSurfing instance, Camera camera, Matrix4f modelViewMatrix,
                                          Matrix4f projectionMatrix, boolean isCrosshairDynamic, float partialTick,
                                          Operation<Vec2f> original) {
        if (CoOConfig.shouldersurfingSkipIdleCrosshairPick && !isCrosshairDynamic) {
            CrosshairConfig crosshair = Config.CLIENT.getCrosshairConfig();
            if (!crosshair.isObstructionIndicatorEnabled()
                    || (crosshair.isObstructionIndicatorOnlyShownWhenAiming() && !instance.isAiming())) {
                return null;
            }
        }
        return original.call(instance, camera, modelViewMatrix, projectionMatrix, isCrosshairDynamic, partialTick);
    }
}
