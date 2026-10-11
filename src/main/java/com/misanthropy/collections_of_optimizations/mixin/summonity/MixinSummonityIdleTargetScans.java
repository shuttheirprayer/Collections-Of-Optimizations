package com.misanthropy.collections_of_optimizations.mixin.summonity;

import com.fevzi.summonity.entity.SummonedCopperGolemEntity;
import com.fevzi.summonity.entity.SummonedCrystalWispEntity;
import com.fevzi.summonity.entity.SummonedGnomeEntity;
import com.fevzi.summonity.entity.SummonedImpEntity;
import com.fevzi.summonity.entity.SummonedMagicMirrorEntity;
import com.fevzi.summonity.entity.SummonedMiniatureSunEntity;
import com.fevzi.summonity.entity.SummonedParrotEntity;
import com.fevzi.summonity.entity.SummonedPrismaticLanternEntity;
import com.fevzi.summonity.entity.SummonedSanguineBatEntity;
import com.fevzi.summonity.entity.SummonedSculkMiteEntity;
import com.fevzi.summonity.entity.SummonedSimpleTurretEntity;
import com.fevzi.summonity.entity.SummonedSlimeEntity;
import com.fevzi.summonity.entity.SummonedSpiderEntity;
import com.fevzi.summonity.entity.SummonedStardustDragonEntity;
import com.fevzi.summonity.entity.SummonedUltraSnifferEntity;
import com.fevzi.summonity.entity.SummonedVirentiumCubeEntity;
import com.fevzi.summonity.entity.SummonedWarriorsArsenalEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(
        value = {
                SummonedCopperGolemEntity.class,
                SummonedCrystalWispEntity.class,
                SummonedGnomeEntity.class,
                SummonedImpEntity.class,
                SummonedMagicMirrorEntity.class,
                SummonedMiniatureSunEntity.class,
                SummonedParrotEntity.class,
                SummonedPrismaticLanternEntity.class,
                SummonedSanguineBatEntity.class,
                SummonedSculkMiteEntity.class,
                SummonedSimpleTurretEntity.class,
                SummonedSlimeEntity.class,
                SummonedSpiderEntity.class,
                SummonedStardustDragonEntity.class,
                SummonedUltraSnifferEntity.class,
                SummonedVirentiumCubeEntity.class,
                SummonedWarriorsArsenalEntity.class
        },
        remap = false
)
public abstract class MixinSummonityIdleTargetScans {

    @WrapOperation(
            method = {
                    "getActiveTarget(Lnet/minecraft/world/entity/player/Player;D)Lnet/minecraft/world/entity/LivingEntity;",
                    "getActiveTarget(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/LivingEntity;",
                    "findTarget(Lnet/minecraft/world/entity/player/Player;D)Lnet/minecraft/world/entity/LivingEntity;"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private List<Mob> coo$spreadIdleTargetScans(Level level, Class<Mob> type, AABB box, Predicate<? super Mob> filter, Operation<List<Mob>> original) {
        int interval = CoOConfig.summonityIdleTargetScanInterval;
        if (interval > 1) {
            Entity self = (Entity) (Object) this;
            if (Math.floorMod(self.tickCount + self.getId(), interval) != 0) {
                return List.of();
            }
        }
        return original.call(level, type, box, filter);
    }
}
