package com.misanthropy.collections_of_optimizations.mixin.arsnouveau;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.SkyTextureHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyTextureHandler.class, remap = false)
public abstract class MixinSkyTextureHandler {

    @Unique
    private static int coo$lastSkyTick = Integer.MIN_VALUE;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$throttleSkyTexture(RenderLevelStageEvent event, CallbackInfo ci) {
        int interval = CoOConfig.arsnouveauSkyTextureInterval;
        if (interval <= 0 || !event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SKY) || IrisState.shaderPackInUse()) {
            return;
        }

        if (ClientInfo.skyRenderTarget == null) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        int elapsed = player.tickCount - coo$lastSkyTick;
        if (elapsed >= 0 && elapsed < interval) {
            ci.cancel();
            return;
        }
        coo$lastSkyTick = player.tickCount;
    }
}
