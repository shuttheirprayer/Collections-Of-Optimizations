package com.misanthropy.collections_of_optimizations.mixin.arselemental;

import alexthw.ars_elemental.event.ShieldEvents;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShieldEvents.class, remap = false)
public class MixinAEShieldEvents {

    @Inject(method = "onSonicImpact", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipNonSonicMirrorRoll(LivingAttackEvent event, CallbackInfo ci) {
        if (!CoOConfig.arselementalSkipNonSonicMirrorRoll) {
            return;
        }
        if (!event.getSource().is(DamageTypes.SONIC_BOOM)) {
            ci.cancel();
        }
    }
}
