package com.misanthropy.collections_of_optimizations.mixin.asyncparticles;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ParticleShaderSetup;
import forge.fun.qu_an.minecraft.asyncparticles.client.core.particle.gpu_acceleration.IParticleRenderer;
import forge.fun.qu_an.minecraft.asyncparticles.client.core.particle.gpu_acceleration.opengl.GlTfParticleRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GlTfParticleRenderer.class, remap = false)
public abstract class MixinGpuParticleShaderSetup {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lforge/fun/qu_an/minecraft/asyncparticles/client/core/particle/gpu_acceleration/IParticleRenderer;prepareShader(Lnet/minecraft/client/renderer/ShaderInstance;)V"
            ),
            require = 0
    )
    private void coo$constantSamplerNames(ShaderInstance shader) {
        if (!CoOConfig.asyncparticlesConstantSamplerNames) {
            IParticleRenderer.prepareShader(shader);
            return;
        }
        ParticleShaderSetup.prepare(shader);
    }
}
