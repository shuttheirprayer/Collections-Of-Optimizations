package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LionfishFluidWalk;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinLionfishFluidCollision {

    @TargetHandler(
            mixin = "com.github.L_Ender.lionfishapi.mixin.EntityMixin",
            name = "fluidCollision"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private Vec3 coo$leanFluidCollision(Vec3 original, Operation<Vec3> operation) {
        if (!CoOConfig.lionfishapiLeanFluidCollision) {
            return operation.call(original);
        }
        return LionfishFluidWalk.collide((Entity) (Object) this, original);
    }
}
