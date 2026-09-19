package com.misanthropy.collections_of_optimizations.mixin.blessfulled;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.aqutheseal.blessfulled.triggers.SpecialSoundsTriggers", remap = false)
public abstract class MixinBlessfulledSwingSound {

    @Inject(
            method = "playSwingSound",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdleSwingSound(Player player, boolean strong, CallbackInfo ci) {
        if (!CoOConfig.blessfulledSkipIdleSwingSound) {
            return;
        }
        if (!strong) {
            ci.cancel();
        }
    }
}
