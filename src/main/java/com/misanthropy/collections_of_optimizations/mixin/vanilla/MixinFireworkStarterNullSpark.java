package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.particle.FireworkParticles;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireworkParticles.Starter.class)
public abstract class MixinFireworkStarterNullSpark {

    @WrapMethod(method = "createParticle")
    private void coo$skipCancelledSpark(double x, double y, double z, double xd, double yd, double zd,
                                        int[] colors, int[] fadeColors, boolean trail, boolean flicker,
                                        Operation<Void> original) {
        try {
            original.call(x, y, z, xd, yd, zd, colors, fadeColors, trail, flicker);
        } catch (NullPointerException ignored) {
        }
    }
}
