package com.misanthropy.collections_of_optimizations.mixin.arsnspells;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.otectus.arsnspells.events.RegenSynergyHandler", remap = false)
public abstract class MixinAnsSourceJarSynergyTick {

    @Inject(
            method = "onPlayerTick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipDeadSynergyTicks(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.arsnspellsLeanSourceJarTick) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            ci.cancel();
            return;
        }
        Player player = event.player;
        if (player == null || player.level().isClientSide) {
            ci.cancel();
        }
    }
}
