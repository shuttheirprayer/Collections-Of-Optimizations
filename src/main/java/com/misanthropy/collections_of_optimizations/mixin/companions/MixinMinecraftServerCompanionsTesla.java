package com.misanthropy.collections_of_optimizations.mixin.companions;

import com.misanthropy.collections_of_optimizations.core.CompanionsTeslaState;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServerCompanionsTesla {

    @Inject(method = "tickServer", at = @At("TAIL"), require = 0)
    private void coo$flushCompanionsTesla(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        CompanionsTeslaState.serverTick();
    }
}
