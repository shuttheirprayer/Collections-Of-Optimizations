package com.misanthropy.collections_of_optimizations.mixin.darkdoppelganger;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = "net.bandit.darkdoppelganger.event.ServerEvents", remap = false)
public abstract class MixinDDServerEvents {

    @WrapOperation(
            method = "onSpellCasted",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    ordinal = 2
            ),
            require = 0
    )
    private static List<?> coo$skipDuplicateAreaScan(Level level, Class<?> type, AABB box, Operation<List<?>> original) {
        if (CoOConfig.darkdoppelgangerSkipDuplicateSpellScan) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }
}
