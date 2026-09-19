package com.misanthropy.collections_of_optimizations.mixin.bmd;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BmdBlockCacheState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.cerbon.bosses_of_mass_destruction.block.custom.LevitationBlockEntity", remap = false)
public abstract class MixinBmdLevitationFlightScan {

    @Inject(method = "tickFlight", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipEmptyFlightScan(ServerPlayer player, CallbackInfo ci) {
        if (!CoOConfig.bmdLeanLevitationFlightScan || player == null) {
            return;
        }
        if (player.getAbilities().mayfly) {
            return;
        }
        if (BmdBlockCacheState.skipScan(BmdBlockCacheState.LEVITATION)) {
            ci.cancel();
        }
    }
}
