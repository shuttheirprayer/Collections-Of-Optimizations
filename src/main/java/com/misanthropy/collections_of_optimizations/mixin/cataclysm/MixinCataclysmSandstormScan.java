package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = Sandstorm_Entity.class, remap = false)
public abstract class MixinCataclysmSandstormScan {

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private List<LivingEntity> coo$scanOnDamageTicks(Level level, Class<LivingEntity> type, AABB box,
                                                     Operation<List<LivingEntity>> original) {
        if (CoOConfig.cataclysmScanOnlyOnDamageTicks && ((Entity) (Object) this).tickCount % 3 != 0) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }
}
