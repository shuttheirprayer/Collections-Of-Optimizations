package com.misanthropy.collections_of_optimizations.mixin.notenoughanimations;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "dev.tr7zw.notenoughanimations.animations.hands.PetAnimation", remap = false)
public abstract class MixinNeaPetAnimation {

    @Unique
    private Entity coo$petScanOwner;

    @Unique
    private int coo$petScanTick = -1;

    @Unique
    private EntityHitResult coo$petScanResult;

    @WrapOperation(
            method = "isValid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;m_37287_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
            ),
            require = 0
    )
    private EntityHitResult coo$cachePetScan(Entity shooter, Vec3 from, Vec3 to, AABB box,
                                             Predicate<Entity> filter, double range,
                                             Operation<EntityHitResult> original) {
        if (!CoOConfig.notenoughanimationsCachePetScan || shooter == null) {
            return original.call(shooter, from, to, box, filter, range);
        }
        int tick = shooter.tickCount;
        if (this.coo$petScanOwner == shooter && this.coo$petScanTick == tick) {
            return this.coo$petScanResult;
        }
        this.coo$petScanOwner = null;
        this.coo$petScanResult = null;
        EntityHitResult result = original.call(shooter, from, to, box, filter, range);
        this.coo$petScanOwner = shooter;
        this.coo$petScanTick = tick;
        this.coo$petScanResult = result;
        return result;
    }
}
