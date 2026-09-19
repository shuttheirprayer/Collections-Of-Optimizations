package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FdBossesClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.finderfeed.fdbosses.content.items.chesed.PhaseSphereHandler", remap = false)
public abstract class MixinFdBossesPhaseSphere {

    @Inject(
            method = "onChesedItemUse",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$phaseSphereLocalPlayerOnly(Player player, CallbackInfo ci) {
        if (!CoOConfig.fdbossesLocalOnlyPhaseSphereTick) {
            return;
        }
        if (player instanceof ServerPlayer || !player.level().isClientSide()) {
            return;
        }
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        if (coo$isLocalPlayer(player)) {
            return;
        }
        ci.cancel();
    }

    @Unique
    private static boolean coo$isLocalPlayer(Player player) {
        return FdBossesClientPlayer.isLocalPlayer(player);
    }
}
