package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public abstract class MixinFogRendererBiomeFog {

    @Unique
    private static Vec3 coo$biomeFog;

    @Unique
    private static int coo$biomeFogStamp = Integer.MIN_VALUE;

    @WrapOperation(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
            ),
            require = 0
    )
    private static Vec3 coo$memoBiomeFog(Vec3 pos, CubicSampler.Vec3Fetcher fetcher, Operation<Vec3> original) {
        if (!CoOConfig.vanillaMemoBiomeFogColour) {
            return original.call(pos, fetcher);
        }
        int stamp = ClientTickStamp.currentOnRenderThread();
        Vec3 cached = coo$biomeFog;
        if (cached != null && stamp != -1 && stamp == coo$biomeFogStamp) {
            return cached;
        }
        Vec3 value = original.call(pos, fetcher);
        coo$biomeFog = value;
        coo$biomeFogStamp = stamp;
        return value;
    }
}
