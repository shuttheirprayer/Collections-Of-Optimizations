package com.misanthropy.collections_of_optimizations.mixin.asyncparticles;

import forge.fun.qu_an.minecraft.asyncparticles.client.addon.LightCachedParticleAddon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Particle.class, priority = 5050)
public abstract class MixinParticleLeanLightCache {

    @Shadow
    protected ClientLevel level;

    @Shadow
    protected double x;

    @Shadow
    protected double y;

    @Shadow
    protected double z;

    @Overwrite
    public int getLightColor(float partialTick) {
        LightCachedParticleAddon addon = (LightCachedParticleAddon) this;
        if (addon.asyncparticles$isEnabledLightCache()) {
            return addon.asyncparticles$getCachedLight();
        }
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(pos) ? LevelRenderer.getLightColor(this.level, pos) : 0;
    }
}
