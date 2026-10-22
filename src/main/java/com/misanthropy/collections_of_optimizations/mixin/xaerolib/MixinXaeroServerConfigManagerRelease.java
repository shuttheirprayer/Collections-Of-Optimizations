package com.misanthropy.collections_of_optimizations.mixin.xaerolib;

import com.misanthropy.collections_of_optimizations.core.ClientLevelRelease;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "xaero.lib.common.config.server.ServerConfigManager", remap = false)
public abstract class MixinXaeroServerConfigManagerRelease {

    @Inject(method = "setServer", at = @At("HEAD"), require = 0)
    private void coo$trackForRelease(MinecraftServer server, CallbackInfo ci) {
        ClientLevelRelease.trackServerHolder(this, "server");
    }
}
