package com.misanthropy.collections_of_optimizations.mixin.gtbcsgeomancy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.gametechbc.gtbcs_geomancy_plus.effects.Casting.CastingHandler", remap = false)
public abstract class MixinGeomancyCastingTick {

    @Inject(
            method = "onEntityCast",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipNonCasterCastingTick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.gtbcsgeomancySkipNonCasterCastingTick) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof AbstractSpellCastingMob) || entity.level().isClientSide()) {
            ci.cancel();
        }
    }
}
