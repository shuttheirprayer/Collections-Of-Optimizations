package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtSizeScaleCache;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftScaleWindowReset {

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void coo$closeScaleWindow(CallbackInfo ci) {
        FtSizeScaleCache.rendering = false;
    }
}
