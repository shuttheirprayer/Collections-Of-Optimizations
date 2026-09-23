package com.misanthropy.collections_of_optimizations.mixin.l2library;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2library.base.effects.ClientEffectCap;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.events.ClientEffectRenderEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientEffectRenderEvents.class, remap = false)
public abstract class MixinL2LibraryEffectIconScan {

    @Inject(
            method = "onLivingRenderEvents(Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdleIconScan(LivingEntity entity, CallbackInfo ci) {
        if (!CoOConfig.l2libraryLeanEffectIconScan) {
            return;
        }
        if (entity == null) {
            ci.cancel();
            return;
        }

        if (L2LibraryConfig.CLIENT_SPEC.isLoaded() && !L2LibraryConfig.CLIENT.renderOverlayIcons.get()) {
            ci.cancel();
            return;
        }

        ClientEffectCap cap = entity.getCapability(ClientEffectCap.CAPABILITY).orElse(null);
        if (cap == null || cap.map.isEmpty()) {
            ci.cancel();
        }
    }
}
