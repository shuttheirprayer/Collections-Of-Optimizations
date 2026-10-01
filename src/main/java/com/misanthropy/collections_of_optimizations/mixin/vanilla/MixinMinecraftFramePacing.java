package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftFramePacing {

    @Shadow
    @Final
    public Options options;

    @Shadow
    protected abstract int getFramerateLimit();

    @Inject(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateDisplay()V")
    )
    private void coo$limitBeforeSwap(boolean renderLevel, CallbackInfo ci, @Share("coo$paced") LocalBooleanRef paced) {
        if (!CoOConfig.vanillaPaceFramesBeforeSwap || this.options.enableVsync().get()) {
            return;
        }
        paced.set(true);
        int limit = this.getFramerateLimit();
        if (limit < 260) {
            RenderSystem.limitDisplayFPS(limit);
        }
    }

    @WrapWithCondition(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;limitDisplayFPS(I)V")
    )
    private boolean coo$skipLimitAfterSwap(int limit, @Share("coo$paced") LocalBooleanRef paced) {
        return !paced.get();
    }
}
