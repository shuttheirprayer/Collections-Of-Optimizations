package com.misanthropy.collections_of_optimizations.mixin.blessfulled;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.aqutheseal.blessfulled.triggers.HurtOverlayTriggers", remap = false)
public abstract class MixinBlessfulledHurtOverlay {

    @Inject(
            method = "shouldRenderOverlay",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$noOverlayWithoutDamageSource(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.blessfulledFixGeckolibHurtOverlay) {
            return;
        }
        if (source == null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "renderOverlay",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipInvisibleHurtOverlay(LivingEntityRenderer<?, ?> renderer, LivingEntity entity,
                                                     PoseStack poseStack, MultiBufferSource bufferSource,
                                                     CallbackInfo ci) {
        if (!CoOConfig.blessfulledSkipInvisibleHurtOverlay) {
            return;
        }
        if (entity == null || entity.hurtTime <= 0) {
            ci.cancel();
        }
    }
}
