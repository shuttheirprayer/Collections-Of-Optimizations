package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Wither_Homing_Missile_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Wither_Homing_Missile_Entity.class, remap = false)
public abstract class MixinCataclysmWitherMissileFuse {

    @Shadow @Final private static EntityDataAccessor<Integer> FUSE;

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/entity/projectile/Wither_Homing_Missile_Entity;setFuse(I)V"),
            require = 0
    )
    private void coo$unsyncedFuse(Wither_Homing_Missile_Entity self, int fuse, Operation<Void> original) {
        Entity me = (Entity) (Object) this;
        if (!CoOConfig.cataclysmUnsyncedMissileFuse || me.level().isClientSide) {
            original.call(self, fuse);
            return;
        }
        ((CataclysmSynchedEntityDataInvoker) me.getEntityData()).coo$getItem(FUSE).setValue(fuse);
    }
}
