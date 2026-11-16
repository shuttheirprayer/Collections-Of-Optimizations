package com.misanthropy.collections_of_optimizations.mixin.revelationfix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinEntityFluidStandScan {

    @TargetHandler(
            mixin = "com.mega.revelationfix.mixin.EntityMixin",
            name = "fluidCollision"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;m_20193_()Lnet/minecraft/world/level/Level;"
            ),
            cancellable = true,
            remap = false,
            require = 0,

            expect = 0
    )
    private void coo$skipMobFluidStandScan(Vec3 original, CallbackInfoReturnable<Vec3> cir) {
        if (CoOConfig.revelationfixSkipMobFluidStandScan && !((Object) this instanceof Player)) {
            cir.setReturnValue(original);
        }
    }
}
