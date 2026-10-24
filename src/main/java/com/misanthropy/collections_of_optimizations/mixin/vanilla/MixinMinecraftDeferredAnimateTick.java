package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftDeferredAnimateTick {

    @Shadow
    public ClientLevel level;

    @Unique
    private ClientLevel coo$animateLevel;

    @Unique
    private int coo$animateTicks;

    @Unique
    private int coo$animateX;

    @Unique
    private int coo$animateY;

    @Unique
    private int coo$animateZ;

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;animateTick(III)V")
    )
    private void coo$deferAnimateTick(ClientLevel target, int x, int y, int z, Operation<Void> original) {
        if (!CoOConfig.vanillaDeferAmbientParticles) {
            original.call(target, x, y, z);
            return;
        }
        if (this.coo$animateLevel != target) {
            this.coo$animateTicks = 0;
        }
        this.coo$animateLevel = target;
        this.coo$animateX = x;
        this.coo$animateY = y;
        this.coo$animateZ = z;
        if (this.coo$animateTicks < 2) {
            this.coo$animateTicks++;
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void coo$runDeferredAnimateTick(boolean renderLevel, CallbackInfo ci) {
        int ticks = this.coo$animateTicks;
        if (ticks == 0) {
            return;
        }
        ClientLevel target = this.coo$animateLevel;
        this.coo$animateTicks = 0;
        this.coo$animateLevel = null;
        if (target == null || target != this.level) {
            return;
        }
        for (int index = 0; index < ticks; index++) {
            target.animateTick(this.coo$animateX, this.coo$animateY, this.coo$animateZ);
        }
    }
}
