package com.misanthropy.collections_of_optimizations.mixin.mutantmonsters;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MutantMonstersSpawnCount;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Pseudo
@Mixin(targets = "fuzs.mutantmonsters.handler.SpawningPreventionHandler", remap = false)
public abstract class MixinMutantMonstersSpawnLimit {

    @SuppressWarnings("rawtypes")
    @WrapOperation(
            method = "onEntitySpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;count()J"
            ),
            require = 0
    )
    private static long coo$reuseSpawnCount(Stream stream, Operation<Long> original,
                                            Entity entity, ServerLevel level, MobSpawnType spawnType) {
        if (!CoOConfig.mutantmonstersLeanSpawnLimitScan || entity == null || level == null) {
            return original.call(stream);
        }
        return MutantMonstersSpawnCount.count(level, entity.getType(), stream, original);
    }
}
