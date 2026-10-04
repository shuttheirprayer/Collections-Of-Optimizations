package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CataclysmEmpScan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = Death_Laser_Beam_Entity.class, remap = false)
public abstract class MixinCataclysmDeathLaserBeam {

    @WrapOperation(
            method = "raytraceEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private List<LivingEntity> coo$skipClientEntityScan(Level level, Class<LivingEntity> type, AABB box,
                                                        Operation<List<LivingEntity>> original) {
        if (CoOConfig.cataclysmSkipClientBeamEntityScan && level.isClientSide) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;m_121976_(IIIIII)Ljava/lang/Iterable;", ordinal = 1),
            require = 0
    )
    private Iterable<BlockPos> coo$skipEmpScanWithoutEmp(int x0, int y0, int z0, int x1, int y1, int z1,
                                                         Operation<Iterable<BlockPos>> original) {
        if (CoOConfig.cataclysmSkipEmpScanWithoutEmp
                && !CataclysmEmpScan.mayContainEmp(((Entity) (Object) this).level(), x0, y0, z0, x1, y1, z1)) {
            return Collections.emptyList();
        }
        return original.call(x0, y0, z0, x1, y1, z1);
    }
}
