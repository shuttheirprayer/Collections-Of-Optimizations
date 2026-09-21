package com.misanthropy.collections_of_optimizations.mixin.gtbcsgeomancy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.GeomancyGrandmasterTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = "com.gametechbc.gtbcs_geomancy_plus.entity.mobs.geo_grandmaster.GeoGrandmasterEntity", remap = false)
public abstract class MixinGeomancyGrandmasterSpawn {

    @WrapOperation(
            method = "checkGrandmasterSpawnRules",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private static List<Entity> coo$skipGrandmasterSpacingScan(ServerLevel level, Class<Entity> type, AABB box,
                                                               Operation<List<Entity>> original) {
        if (CoOConfig.gtbcsgeomancyGateGrandmasterSpawnScan && GeomancyGrandmasterTracker.noneLoaded()) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }
}
