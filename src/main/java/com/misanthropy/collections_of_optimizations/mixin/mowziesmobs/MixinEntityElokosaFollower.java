package com.misanthropy.collections_of_optimizations.mixin.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.entity.elokosa.EntityElokosaFollower;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityElokosaFollower.class, remap = false)
public abstract class MixinEntityElokosaFollower {

    @Shadow
    protected LivingEntity leader;

    @WrapMethod(method = "getLeader", require = 0)
    private LivingEntity coo$reuseKnownLeader(Operation<LivingEntity> original) {
        LivingEntity known = this.leader;
        if (CoOConfig.mowziesmobsReuseKnownLeader && known != null && !known.isRemoved()) {
            return known;
        }
        return original.call();
    }
}
