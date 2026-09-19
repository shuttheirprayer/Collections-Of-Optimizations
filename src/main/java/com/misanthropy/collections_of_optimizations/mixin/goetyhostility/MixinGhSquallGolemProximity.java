package com.misanthropy.collections_of_optimizations.mixin.goetyhostility;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = "com.ratrod.goetyhostility.common.entities.hostile.HostileSquallGolem", remap = false)
public abstract class MixinGhSquallGolemProximity {

    @WrapOperation(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<LivingEntity> coo$skipSettledProximityScan(Level level, Class<LivingEntity> type, AABB box, Operation<List<LivingEntity>> original) {
        if (CoOConfig.goetyhostilityLeanGolemProximityScan
                && (Object) this instanceof Mob mob
                && mob.getTarget() != null) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }
}
