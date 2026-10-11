package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import net.kayn.fallen_traits.content.item.curio.HandOfCreation;
import net.kayn.fallen_traits.init.FTConfig;
import net.kayn.fallen_traits.init.FTItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectInstance.class)
public abstract class MixinMobEffectInstanceDotTick {

    @Shadow
    @Final
    private MobEffect effect;

    @ModifyExpressionValue(
            method = "tick(Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/Runnable;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;isDurationEffectTick(II)Z"),
            require = 0
    )
    private boolean coo$forceOwnedDotTick(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        if (original || this.effect.getCategory() != MobEffectCategory.HARMFUL) {
            return original;
        }
        LivingEntity owner = HandOfCreation.getEffectOwner(entity, (long) FTConfig.COMMON.handOfCreationDotOwnerWindowTicks.get().intValue());
        return owner != null && CurioCompat.hasItemInCurio(owner, FTItems.HAND_OF_CREATION.get());
    }
}
