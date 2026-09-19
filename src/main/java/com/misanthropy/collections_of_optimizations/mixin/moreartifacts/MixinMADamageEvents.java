package com.misanthropy.collections_of_optimizations.mixin.moreartifacts;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MoreArtifactsMapPurge;
import net.gobies.moreartifacts.event.DamageEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DamageEvents.class, remap = false)
public abstract class MixinMADamageEvents {

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanEquipTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        Player player = event.player;
        if (player == null) {
            return;
        }
        if (CoOConfig.moreartifactsPruneDamageMaps && player.tickCount % 600 == 0) {
            MoreArtifactsMapPurge.sweep();
        }
        if (CoOConfig.moreartifactsTickEndPhaseOnly && event.phase != TickEvent.Phase.END) {
            ci.cancel();
        }
    }
}
