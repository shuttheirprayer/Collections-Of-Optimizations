package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtEntityState;
import net.kayn.fallen_traits.content.item.curio.HandOfCreation;
import net.kayn.fallen_traits.content.item.curio.RageGlove;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {RageGlove.class, HandOfCreation.class}, remap = false)
public abstract class MixinFtStreakUserMark {

    @Inject(method = "getStack", at = @At("HEAD"), require = 0)
    private void coo$markStreakUser(LivingEntity user, CallbackInfoReturnable<?> cir) {
        if (user instanceof FtEntityState state) {
            state.coo$ftMarkStreakUser();
        }
    }
}
