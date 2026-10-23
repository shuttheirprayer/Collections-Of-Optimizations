package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.misanthropy.collections_of_optimizations.core.TouchNMotionStride;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import strm.emfcompat.core.ik.IKFrame;
import strm.touchnmotion.torso.Stride;

@Mixin(value = Stride.class, remap = false)
public abstract class MixinStrideProbe {

    @Redirect(method = "level(Lnet/minecraft/client/player/AbstractClientPlayer;Lstrm/emfcompat/core/ik/IKFrame;Lnet/minecraft/client/model/geom/ModelPart;Lorg/joml/Vector3f;Lorg/joml/Vector3f;)Z",
            at = @At(value = "INVOKE", target = "Lstrm/touchnmotion/torso/Stride;level(Lnet/minecraft/client/player/AbstractClientPlayer;Lstrm/emfcompat/core/ik/IKFrame;Lnet/minecraft/client/model/geom/ModelPart;Lorg/joml/Vector3f;Lorg/joml/Vector3f;DDD)Z"))
    private static boolean coo$probeWithoutClipContexts(AbstractClientPlayer player, IKFrame frame, ModelPart leg, Vector3f from, Vector3f to,
                                                       double spread, double probe, double tolerance) {
        return TouchNMotionStride.levelOrStock(player, frame, leg, from, to, spread, probe, tolerance);
    }
}
