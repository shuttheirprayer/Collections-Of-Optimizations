package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.lionfishapi.server.animation.LegSolver;
import com.github.L_Ender.lionfishapi.server.animation.LegSolverQuadruped;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ancient_Ancient_Remnant_Entity",
        "com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Ancient_Remnant.Ancient_Remnant_Entity",
        "com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AcropolisMonsters.Clawdian_Entity"
}, remap = false)
public abstract class MixinCataclysmServerLegSolver {

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE",
                    target = "Lcom/github/L_Ender/lionfishapi/server/animation/LegSolver;update(Lnet/minecraft/world/entity/LivingEntity;FF)V"),
            require = 0
    )
    private void coo$clientOnlyLegs(LegSolver solver, LivingEntity entity, float yaw, float scale, Operation<Void> original) {
        if (CoOConfig.cataclysmSkipServerLegSolver && !entity.level().isClientSide) {
            return;
        }
        original.call(solver, entity, yaw, scale);
    }

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE",
                    target = "Lcom/github/L_Ender/lionfishapi/server/animation/LegSolverQuadruped;update(Lnet/minecraft/world/entity/LivingEntity;FF)V"),
            require = 0
    )
    private void coo$clientOnlyQuadLegs(LegSolverQuadruped solver, LivingEntity entity, float yaw, float scale, Operation<Void> original) {
        if (CoOConfig.cataclysmSkipServerLegSolver && !entity.level().isClientSide) {
            return;
        }
        original.call(solver, entity, yaw, scale);
    }
}
