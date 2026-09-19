package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VecDeltaCodec.class)
public abstract class MixinVecDeltaCodec {

    @Shadow
    private Vec3 base;

    @WrapMethod(method = "delta", require = 0)
    private Vec3 coo$skipZeroDelta(Vec3 position, Operation<Vec3> original) {
        if (CoOConfig.vanillaLeanTrackerDelta) {
            Vec3 base = this.base;
            if (position.x == base.x && position.y == base.y && position.z == base.z) {
                return Vec3.ZERO;
            }
        }
        return original.call(position);
    }
}
