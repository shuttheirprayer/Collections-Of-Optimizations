package com.misanthropy.collections_of_optimizations.mixin.tact;

import com.github.alexmodguy.alexscaves.server.misc.CaveBookProgress;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.telepathicgrunt.tact.CompendiumUnlock", remap = false)
public abstract class MixinTactCompendiumUnlockSaves {

    @Unique
    private static CaveBookProgress coo$pendingProgress;

    @Unique
    private static Player coo$pendingPlayer;

    @Inject(
            method = "playerLoggedIn(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V",
            at = @At("HEAD"),
            remap = false,
            require = 0
    )
    private static void coo$dropStaleCompendiumSave(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        coo$pendingProgress = null;
        coo$pendingPlayer = null;
    }

    @WrapOperation(
            method = "playerLoggedIn(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/alexmodguy/alexscaves/server/misc/CaveBookProgress;saveCaveBookProgress(Lcom/github/alexmodguy/alexscaves/server/misc/CaveBookProgress;Lnet/minecraft/world/entity/player/Player;)V"
            ),
            remap = false,
            require = 0
    )
    private static void coo$deferCompendiumSave(CaveBookProgress progress, Player player, Operation<Void> original) {
        if (!CoOConfig.tactBatchCompendiumUnlockSave) {
            original.call(progress, player);
            return;
        }
        coo$pendingProgress = progress;
        coo$pendingPlayer = player;
    }

    @Inject(
            method = "playerLoggedIn(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V",
            at = @At("RETURN"),
            remap = false,
            require = 0
    )
    private static void coo$flushCompendiumSave(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        CaveBookProgress progress = coo$pendingProgress;
        Player player = coo$pendingPlayer;
        coo$pendingProgress = null;
        coo$pendingPlayer = null;
        if (progress != null && player != null) {
            CaveBookProgress.saveCaveBookProgress(progress, player);
        }
    }
}
