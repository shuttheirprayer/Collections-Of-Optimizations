package com.misanthropy.collections_of_optimizations.mixin.timm;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Sound.class)
public abstract class MixinTimmSoundStream {

    @Shadow
    public abstract ResourceLocation getLocation();

    @ModifyReturnValue(method = "shouldStream", at = @At("RETURN"))
    private boolean coo$streamTimmMusic(boolean stream) {
        return stream || CoOConfig.timmStreamMusic && "timm".equals(getLocation().getNamespace());
    }
}
