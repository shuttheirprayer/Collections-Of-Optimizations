package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.alexander.witherreincarnated.events.HandleAnimationsEvent;
import com.alexander.witherreincarnated.events.ParticleSpawnersEvent;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.WrState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(value = ParticleSpawnersEvent.class, remap = false)
public abstract class MixinWrParticleSpawners {

    @Redirect(
            method = "renderGlows(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/multiplayer/ClientLevel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;", remap = true)
    )
    private static Iterable<Entity> coo$skipGlowScanWithoutWithers(ClientLevel level) {
        return WrState.noClientWithers() ? Collections.emptyList() : level.entitiesForRendering();
    }

    @Inject(method = "removeParticles", at = @At("TAIL"))
    private static void coo$forgetWorldOnLeave(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (Minecraft.getInstance().level == null) {
            WrState.clientLevelGone();
            if (CoOConfig.wrFixAnimationLeak) {
                HandleAnimationsEvent.witherAnimations.clear();
            }
        }
    }
}
