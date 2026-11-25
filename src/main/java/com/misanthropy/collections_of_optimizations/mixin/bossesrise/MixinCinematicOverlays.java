package com.misanthropy.collections_of_optimizations.mixin.bossesrise;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BossEntityScan;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.unusual.blockfactorysbosses.procedures.CinematicBarDisplayOverlayIngameProcedure;
import net.unusual.blockfactorysbosses.procedures.CinematicBlackEasingProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = {CinematicBarDisplayOverlayIngameProcedure.class, CinematicBlackEasingProcedure.class}, remap = false)
public abstract class MixinCinematicOverlays {

    @WrapOperation(
            method = {
                    "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)Z",
                    "execute(Lnet/minecraftforge/eventbus/api/Event;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private static List<Entity> coo$skipWhenNoBossLoaded(LevelAccessor world, Class<Entity> type, AABB box, Predicate<? super Entity> filter,
                                                         Operation<List<Entity>> original) {
        if (CoOConfig.bossesriseNarrowCinematicScan && world instanceof Level level && level.isClientSide() && BossEntityScan.matches(level).isEmpty()) {
            return List.of();
        }
        return original.call(world, type, box, filter);
    }
}
