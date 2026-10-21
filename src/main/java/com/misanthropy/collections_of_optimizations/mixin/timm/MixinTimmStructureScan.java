package com.misanthropy.collections_of_optimizations.mixin.timm;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 1500)
public abstract class MixinTimmStructureScan {
    // structure scan is gg
    @TargetHandler(mixin = "com.github.charlyb01.timm.mixin.ServerPlayerMixin", name = "onTick")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipStructureScan(CallbackInfo original, CallbackInfo ci) {
        if (CoOConfig.timmSkipStructureScan) {
            ci.cancel();
        }
    }
}
