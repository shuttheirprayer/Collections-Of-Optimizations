package com.misanthropy.collections_of_optimizations.mixin.l2library;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2library.capability.conditionals.ConditionalData;
import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConditionalData.class, remap = false)
public abstract class MixinL2LibraryConditionalTick {

    @Inject(
            method = "tick()V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipEmptyConditionalTick(CallbackInfo ci) {
        if (!CoOConfig.l2librarySkipEmptyConditionalTick) {
            return;
        }
        ConditionalData self = (ConditionalData) (Object) this;
        if (self.data == null || !self.data.isEmpty()) {
            return;
        }

        self.tickSinceDeath++;

        Player player = self.player;
        if (player != null
                && self.tickSinceDeath < 60
                && L2LibraryConfig.COMMON_SPEC.isLoaded()
                && L2LibraryConfig.COMMON.restoreFullHealthOnRespawn.get()
                && player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        ci.cancel();
    }
}
