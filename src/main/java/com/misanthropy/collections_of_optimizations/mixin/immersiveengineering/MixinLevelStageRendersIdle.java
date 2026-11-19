package com.misanthropy.collections_of_optimizations.mixin.immersiveengineering;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(targets = "blusunrize.immersiveengineering.client.LevelStageRenders", remap = false)
public abstract class MixinLevelStageRendersIdle {

    @Shadow
    @Final
    public static Map<?, ?> FAILED_CONNECTIONS;

    @Inject(method = "renderFailedConnections", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipWhenNoFailedConnections(net.minecraftforge.client.event.RenderLevelStageEvent event, CallbackInfo ci) {
        if (CoOConfig.immersiveEngineeringSkipIdleConnectionPass && FAILED_CONNECTIONS.isEmpty()) {
            ci.cancel();
        }
    }
}
