package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.EarthQuake_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = EarthQuake_Entity.class, remap = false)
public abstract class MixinCataclysmEarthQuakeScan {

    @WrapOperation(
            method = "onUpdateInAir",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private List<LivingEntity> coo$scanOnDamageTicks(Level level, Class<LivingEntity> type, AABB box,
                                                     Operation<List<LivingEntity>> original) {
        if (CoOConfig.cataclysmScanOnlyOnDamageTicks) {
            Projectile self = (Projectile) (Object) this;
            if (self.tickCount % 5 != 0 || self.getOwner() == null) {
                return Collections.emptyList();
            }
        }
        return original.call(level, type, box);
    }
}
