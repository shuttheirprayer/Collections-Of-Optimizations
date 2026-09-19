package com.misanthropy.collections_of_optimizations.mixin.notenoughanimations;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.tr7zw.notenoughanimations.renderlayer.SwordRenderLayer", remap = false)
public abstract class MixinNeaSwordRenderLayer {

    @Inject(
            method = "update(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipServerSideSwordTracking(Player player, CallbackInfo ci) {
        if (!CoOConfig.notenoughanimationsSkipServerSwordTracking || player == null) {
            return;
        }
        Level level = player.level();
        if (level != null && !level.isClientSide()) {
            ci.cancel();
        }
    }
}
