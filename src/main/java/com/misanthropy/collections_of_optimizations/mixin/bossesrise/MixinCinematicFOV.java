package com.misanthropy.collections_of_optimizations.mixin.bossesrise;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BossEntityScan;
import com.misanthropy.collections_of_optimizations.core.ModEntityFilter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.Event;
import net.unusual.blockfactorysbosses.procedures.CinematicFOVProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = CinematicFOVProcedure.class, remap = false)
public abstract class MixinCinematicFOV {

    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDDD)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipWhenNoBossLoaded(Event event, LevelAccessor world, double x, double y, double z,
                                                 double fov, double partialTick, CallbackInfo ci) {
        if (CoOConfig.bossesriseNarrowCinematicScan
                && world instanceof Level level
                && level.isClientSide()
                && BossEntityScan.matches(level).isEmpty()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private static List<Entity> coo$onlyCollectBossesRiseEntities(LevelAccessor level, Class<Entity> type, AABB box,
                                                                  Predicate<? super Entity> filter,
                                                                  Operation<List<Entity>> original) {
        if (!CoOConfig.bossesriseNarrowCinematicScan) {
            return original.call(level, type, box, filter);
        }
        Predicate<? super Entity> narrowed =
                (Predicate<Entity>) entity -> ModEntityFilter.BOSSES_RISE.matches(entity) && filter.test(entity);
        return original.call(level, type, box, narrowed);
    }
}
