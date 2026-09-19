package com.misanthropy.collections_of_optimizations.mixin.brutalbosses;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.brutalbosses.event.EventHandler", remap = false)
public abstract class MixinBrutalBossesEventHandler {

    @Inject(method = "attachCapabilities", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipNonLivingBossCapability(AttachCapabilitiesEvent<Entity> event, CallbackInfo ci) {
        if (!CoOConfig.brutalbossesSkipNonLivingBossCapability) {
            return;
        }
        if (!(event.getObject() instanceof LivingEntity)) {
            ci.cancel();
        }
    }
}
