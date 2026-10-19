package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.OldUsersConverter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(OldUsersConverter.class)
public abstract class MixinOldUsersConverterEmptyOwner {

    @Inject(
            method = "convertMobOwnerIfNecessary(Lnet/minecraft/server/MinecraftServer;Ljava/lang/String;)Ljava/util/UUID;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipEmptyOwner(MinecraftServer server, String name, CallbackInfoReturnable<UUID> cir) {
        if (CoOConfig.vanillaSkipEmptyOwnerUuidParse && name != null && name.isEmpty()) {
            cir.setReturnValue(null);
        }
    }
}
