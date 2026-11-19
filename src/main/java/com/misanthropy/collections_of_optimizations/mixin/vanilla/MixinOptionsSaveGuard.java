package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.client.Options;
import net.minecraftforge.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class MixinOptionsSaveGuard {

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void coo$keepOptionsOnFailedLoad(CallbackInfo ci) {
        if (!ModLoader.isLoadingStateValid()) {
            ci.cancel();
        }
    }
}
