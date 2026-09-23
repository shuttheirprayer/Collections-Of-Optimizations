package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Pseudo
@Mixin(targets = "com.Polarice3.goety_cataclysm.init.GCGolemTypes", remap = false)
public abstract class MixinGcGolemTypes {

    @Unique
    private static final AtomicBoolean coo$added = new AtomicBoolean();

    @Inject(method = "addGolems", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$addOnce(CallbackInfo ci) {
        if (coo$added.getAndSet(true) && CoOConfig.goetycataclysmRegisterGolemsOnce) {
            ci.cancel();
        }
    }
}
