package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Ashen_Breath_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = Ashen_Breath_Entity.class, remap = false)
public abstract class MixinCataclysmAshenBreathScan {

    @WrapOperation(
            method = "hitEntities",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/entity/projectile/Ashen_Breath_Entity;getEntityLivingBaseNearby(DDDD)Ljava/util/List;"),
            require = 0
    )
    private List<LivingEntity> coo$scanOnDamageTicks(Ashen_Breath_Entity self, double dx, double dy, double dz, double r,
                                                     Operation<List<LivingEntity>> original) {
        if (CoOConfig.cataclysmScanOnlyOnDamageTicks && ((Entity) (Object) this).tickCount % 3 != 0) {
            return Collections.emptyList();
        }
        return original.call(self, dx, dy, dz, r);
    }
}
