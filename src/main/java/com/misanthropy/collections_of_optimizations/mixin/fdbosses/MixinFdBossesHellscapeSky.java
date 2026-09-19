package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mixin(targets = "com.finderfeed.fdbosses.BossClientEvents", remap = false)
public abstract class MixinFdBossesHellscapeSky {

    @SuppressWarnings("rawtypes")
    @WrapOperation(
            method = "tickHellscapeSky()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
                    remap = true
            ),
            require = 0
    )
    private static List coo$skipHellscapeSkyScan(Level level, Class type, AABB box, Predicate filter,
                                                 Operation<List> original) {
        if (CoOConfig.fdbossesSkipHellscapeSkyScan && FdBossesPresence.noClientSpawners()) {
            return Collections.emptyList();
        }
        return original.call(level, type, box, filter);
    }
}
