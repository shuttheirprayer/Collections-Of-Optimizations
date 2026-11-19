package com.misanthropy.collections_of_optimizations.mixin.fwa;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.core.FwaLod;
import fr.madu59.fwa.FancyWorldAnimationsClient;
import fr.madu59.fwa.anims.Animation;
import fr.madu59.fwa.rendering.AnimationRenderingContext;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FancyWorldAnimationsClient.class, remap = false)
public abstract class MixinFwaLod {
    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private static void coo$frame(AnimationRenderingContext context, CallbackInfo ci) {
        if (!context.isShadow()) {
            FwaLod.beginFrame(context.getCameraPos());
            com.misanthropy.collections_of_optimizations.core.FwaFastQuads.newFrame();
        }
    }

    @ModifyReturnValue(method = "shouldCancelBlockRendering", at = @At("RETURN"), remap = false)
    private static boolean coo$bakeFar(boolean cancel, BlockPos pos) {
        if (!FwaLod.enabled || !cancel) {
            return cancel;
        }
        Animation animation = FancyWorldAnimationsClient.animations.getAt(pos);
        return animation == null || !animation.hasInfiniteAnimation() || !FwaLod.meshDecision((FwaLod.State) animation, pos);
    }

    @ModifyReturnValue(method = "shouldCancelBlockEntityRendering", at = @At("RETURN"), remap = false)
    private static boolean coo$showFarBlockEntity(boolean cancel, BlockPos pos) {
        if (!FwaLod.enabled || !cancel) {
            return cancel;
        }
        Animation animation = FancyWorldAnimationsClient.animations.getAt(pos);
        return animation == null || ((FwaLod.State) animation).coo$lodState() != FwaLod.BAKED;
    }

    @Inject(method = "renderAnimation", at = @At("HEAD"), cancellable = true, remap = false)
    private static void coo$skipBaked(Animation animation, AnimationRenderingContext context, CallbackInfo ci) {
        if (FwaLod.enabled && animation.hasInfiniteAnimation() && FwaLod.skipRender((FwaLod.State) animation, animation.getPos(), animation.getBoundingBox())) {
            ci.cancel();
        }
    }
}
