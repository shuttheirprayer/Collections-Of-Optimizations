package com.misanthropy.collections_of_optimizations.mixin.timm;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TimmMusicCache;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.github.charlyb01.timm.music.BiomePlaylist", remap = false)
public abstract class MixinTimmBiomePlaylist {

    @Redirect(method = {"getMusicSound", "getCreativeMusic", "getMenuMusic"},
            at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;IIZ)Lnet/minecraft/sounds/Music;"))
    private static Music coo$cachedMusic(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        return CoOConfig.timmCacheMusic
                ? TimmMusicCache.get(event, minDelay, maxDelay, replaceCurrentMusic)
                : new Music(event, minDelay, maxDelay, replaceCurrentMusic);
    }
}
