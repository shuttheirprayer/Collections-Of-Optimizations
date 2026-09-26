package com.misanthropy.collections_of_optimizations.mixin.snuffles;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "mod.schnappdragon.snuffles.common.entity.animal.Snuffle", remap = false)
public abstract class MixinSnuffleFluffRegrow {

    @ModifyExpressionValue(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lmod/schnappdragon/snuffles/common/entity/animal/Snuffle;hasFluff()Z"
            ),
            require = 0
    )
    private boolean coo$serverOnlyFluffRegrow(boolean original) {
        if (original || !CoOConfig.snufflesServerSideFluffRegrow) {
            return original;
        }
        return ((Entity) (Object) this).level().isClientSide;
    }
}
