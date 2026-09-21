package com.misanthropy.collections_of_optimizations.mixin.refinedmod;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.refinedrain.refinedmod.entity.spells.storm_blade.StormBladeCollider", remap = false)
public abstract class MixinStormBladeOrphan {

    @Inject(method = {"onHitEntity", "m_5790_"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$discardOwnerlessCollider(EntityHitResult hit, CallbackInfo ci) {
        if (!CoOConfig.refinedmodDiscardOwnerlessStormBlade) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide || self.isRemoved()) {
            return;
        }
        if (!(self instanceof Projectile projectile)) {
            return;
        }
        if (projectile.getOwner() instanceof LivingEntity) {
            return;
        }
        ci.cancel();
        self.discard();
    }
}
