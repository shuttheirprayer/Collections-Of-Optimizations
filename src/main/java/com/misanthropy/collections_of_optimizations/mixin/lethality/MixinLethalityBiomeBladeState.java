package com.misanthropy.collections_of_optimizations.mixin.lethality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.daphne.lethality.events.BrokenBiomeBladeEvents", remap = false)
public abstract class MixinLethalityBiomeBladeState {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$serverOnlyLifestealState(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.lethalityServerOnlyBiomeBladeState || event == null) {
            return;
        }
        Player player = event.player;
        if (player != null && player.level().isClientSide) {
            ci.cancel();
        }
    }
}
