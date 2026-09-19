package com.misanthropy.collections_of_optimizations.mixin.peyroscythe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "com.rinko1231.peyroscythe.spellentity.ice.FrostFogEntity", remap = false)
public abstract class MixinPeyroFrostFog {

    @WrapOperation(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<LivingEntity> coo$skipClientFrostFogScan(Level level, Class<?> type, AABB box, Predicate<?> filter, Operation<List<LivingEntity>> original) {
        if (CoOConfig.peyroscytheServerOnlyAuraScans && level != null && level.isClientSide) {
            return Collections.emptyList();
        }
        return original.call(level, type, box, filter);
    }
}
