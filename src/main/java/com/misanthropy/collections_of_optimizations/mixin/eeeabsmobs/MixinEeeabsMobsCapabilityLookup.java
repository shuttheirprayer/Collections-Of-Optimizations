package com.misanthropy.collections_of_optimizations.mixin.eeeabsmobs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.eeeab.eeeabsmobs.sever.handler.CapabilityHandler", remap = false)
public abstract class MixinEeeabsMobsCapabilityLookup {

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, require = 0)
    private static <T> void coo$singleLookup(Entity entity, Capability<T> capability, CallbackInfoReturnable<T> cir) {
        if (!CoOConfig.eeeabsmobsSingleCapabilityLookup) {
            return;
        }
        cir.setReturnValue(entity == null || !entity.isAlive() ? null : entity.getCapability(capability).orElse(null));
    }
}
