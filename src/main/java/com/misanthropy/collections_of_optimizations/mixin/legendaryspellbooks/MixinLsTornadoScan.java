package com.misanthropy.collections_of_optimizations.mixin.legendaryspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LsTornadoState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Pseudo
@Mixin(targets = "dev.higurashi.legendary_spellbooks.common.entities.handler.TornadoEntityHandler", remap = false)
public abstract class MixinLsTornadoScan {

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipTornadoScan(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.legendaryspellbooksLeanTornadoScan || event == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (LsTornadoState.tornadoTickedRecently(entity) || entity.tickCount % 20 == 0) {
            return;
        }
        Set<String> tags = entity.getTags();
        if (!tags.isEmpty() && tags.contains("in_tornado")) {
            entity.removeTag("in_tornado");
        }
        ci.cancel();
    }
}
