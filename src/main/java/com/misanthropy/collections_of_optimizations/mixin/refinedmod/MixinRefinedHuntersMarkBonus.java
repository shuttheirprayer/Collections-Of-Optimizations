package com.misanthropy.collections_of_optimizations.mixin.refinedmod;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.refinedrain.refinedmod.effect.hunters_mark.HuntersMarkHandler", remap = false)
public abstract class MixinRefinedHuntersMarkBonus {

    @Inject(
            method = "getBonusModifier(ILnet/minecraft/world/entity/LivingEntity;)F",
            at = @At("RETURN"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$clampHuntersMarkBonus(int amplifier, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (!CoOConfig.refinedmodClampHuntersMarkBonus) {
            return;
        }
        if (cir.getReturnValueF() < 0.0F) {
            cir.setReturnValue(0.0F);
        }
    }
}
