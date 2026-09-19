package com.misanthropy.collections_of_optimizations.mixin.starcatcher;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.UUID;

@Pseudo
@Mixin(targets = "com.wdiscute.starcatcher.tournament.TournamentHandler", remap = false)
public abstract class MixinStarcatcherTournamentProfile {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;get()Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object coo$tolerateMissingWinnerProfile(Optional<Object> optional, Operation<Object> original) {
        if (!CoOConfig.starcatcherTournamentProfileFallback || optional == null || optional.isPresent()) {
            return original.call(optional);
        }
        return new GameProfile(new UUID(0L, 0L), "???");
    }
}
