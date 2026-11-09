package com.misanthropy.collections_of_optimizations.mixin.skarriermobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.mcreator.skarriermobs.procedures.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = {
        BlareOnTickUpdateProcedure.class,
        BowlderOnEntityTickUpdateProcedure.class,
        DangleOnEntityTickUpdateProcedure.class,
        DraggerOnEntityTickUpdateProcedure.class,
        EaterOnEntityTickUpdateProcedure.class,
        EndoraHeadMeleeOnEntityTickUpdateProcedure.class,
        EndorahHeadRangedOnEntityTickUpdateProcedure.class,
        GorgerOnEntityTickUpdateProcedure.class,
        QuakeOnEntityTickUpdateProcedure.class,
        SlitherMatriarchOnEntityTickUpdateProcedure.class,
        WroughtOnEntityTickUpdateProcedure.class
}, remap = false)
public abstract class MixinSkarrierTargetScans {

    @WrapOperation(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private static List<Entity> coo$onlyTestTheCurrentTarget(LevelAccessor level, Class<Entity> type, AABB box,
                                                             Predicate<? super Entity> filter,
                                                             Operation<List<Entity>> original,
                                                             LevelAccessor world, double x, double y, double z,
                                                             Entity self) {
        if (!CoOConfig.skarriermobsLeanTargetProximityScans || type != Entity.class || !(self instanceof Mob mob)) {
            return original.call(level, type, box, filter);
        }
        LivingEntity target = mob.getTarget();
        if (target == null || !target.getBoundingBox().intersects(box)) {
            return List.of();
        }
        return List.of(target);
    }
}
