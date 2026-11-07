package com.misanthropy.collections_of_optimizations.mixin.traveloptics;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "com.gametechbc.traveloptics.effects.Casting.CastingHandler",
        "com.gametechbc.traveloptics.effects.casting.CastingHandler"
}, remap = false)
public abstract class MixinTravelopticsCastingCast {

    @Inject(method = "onEntityCast", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipCastingEffectLookup(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (CoOConfig.travelopticsLeanCastEffectChecks
                && !(event.getEntity() instanceof AbstractSpellCastingMob)) {
            ci.cancel();
        }
    }
}
