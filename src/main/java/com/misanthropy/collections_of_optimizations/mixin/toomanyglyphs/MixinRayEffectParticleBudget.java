package com.misanthropy.collections_of_optimizations.mixin.toomanyglyphs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TmgRayParticleBudget;
import io.github.derringersmods.toomanyglyphs.common.network.PacketRayEffect;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketRayEffect.ClientHandler.class, remap = false)
public abstract class MixinRayEffectParticleBudget {

    @Inject(
            method = "accept(Lio/github/derringersmods/toomanyglyphs/common/network/PacketRayEffect;Lnet/minecraftforge/network/NetworkEvent$Context;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$capRayParticles(PacketRayEffect msg, NetworkEvent.Context context, CallbackInfo ci) {
        int budget = CoOConfig.toomanyglyphsRayParticleBudget;
        if (budget <= 0) {
            return;
        }

        Vec3 from = msg.from;
        Vec3 to = msg.to;
        if (from == null || to == null) {
            return;
        }

        int estimate = (int) Math.min(8192.0, from.distanceTo(to) * 16.0) + 1;
        if (!TmgRayParticleBudget.allow(budget, estimate)) {
            ci.cancel();
        }
    }
}
