package com.misanthropy.collections_of_optimizations.mixin.mekaweapons;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "meranha.mekaweapons.MekaWeapons", remap = false)
public abstract class MixinMekaTanaSweepCooldown {

    @ModifyExpressionValue(
            method = "MekaWeaponsAttackEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lmeranha/mekaweapons/MekaWeaponsUtils;isModuleEnabled(Lnet/minecraft/world/item/ItemStack;Lmekanism/api/providers/IModuleDataProvider;)Z"
            ),
            require = 0
    )
    private boolean coo$sweepNeedsFullSwing(boolean enabled, @Local(argsOnly = true) AttackEntityEvent event) {
        if (!enabled || !CoOConfig.mekaweaponsTanaSweepNeedsFullSwing) {
            return enabled;
        }
        return event.getEntity().getAttackStrengthScale(0.5F) > 0.9F;
    }
}
