package com.misanthropy.collections_of_optimizations.mixin.legendaryspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.higurashi.legendary_spellbooks.common.event.AnnihilationAttributeBonusEvent", remap = false)
public abstract class MixinLsAnnihilationBonusRate {

    @Inject(method = "onLivingTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$throttleBonusRefresh(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        int interval = CoOConfig.legendaryspellbooksAnnihilationBonusInterval;
        if (interval <= 2 || event == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (entity.tickCount % interval != 0) {
            ci.cancel();
        }
    }
}
