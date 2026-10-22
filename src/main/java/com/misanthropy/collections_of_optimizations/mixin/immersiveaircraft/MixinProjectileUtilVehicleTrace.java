package com.misanthropy.collections_of_optimizations.mixin.immersiveaircraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.VehicleCount;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = ProjectileUtil.class, priority = 1500)
public abstract class MixinProjectileUtilVehicleTrace {

    @WrapOperation(
            method = "ia$vehicleTrace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
                    remap = true
            ),
            remap = false,
            require = 0
    )
    private static List<Entity> coo$skipWithoutVehicles(Level level, Entity source, AABB box, Predicate<? super Entity> filter,
                                                     Operation<List<Entity>> original) {
        if (CoOConfig.immersiveaircraftSkipEmptyVehicleTrace && VehicleCount.none(level)) {
            return List.of();
        }
        return original.call(level, source, box, filter);
    }
}
