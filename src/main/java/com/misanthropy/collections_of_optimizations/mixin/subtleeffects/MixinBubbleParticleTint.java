package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BubbleParticle.class, BubbleColumnUpParticle.class, WaterCurrentDownParticle.class}, priority = 1500)
public abstract class MixinBubbleParticleTint {

    @Unique
    private boolean coo$tinted;

    @Inject(
            method = "subtleEffects$setupBubble",
            at = @At(value = "FIELD", target = "subtleEffects$overlaySprite:Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", opcode = Opcodes.PUTFIELD),
            remap = false,
            require = 0
    )
    private void coo$markTinted(CallbackInfo ci) {
        this.coo$tinted = true;
    }

    @TargetHandler(
            mixin = "einstein.subtle_effects.mixin.client.particle.bubbles.BubbleParticleMixin",
            name = "tick"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$skipUnusedTint(CallbackInfo ci, Operation<Void> original) {
        if (CoOConfig.subtleeffectsUnusedBubbleTint && !this.coo$tinted && ((Particle) (Object) this).isAlive()) {
            return;
        }
        original.call(ci);
    }
}
