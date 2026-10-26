package com.misanthropy.collections_of_optimizations.mixin.timm;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TimmMusicCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.github.charlyb01.timm.music.BiomePlaylist", remap = false)
public abstract class MixinTimmBiomePlaylist {

    @Shadow
    public static Music getMusicSound(ResourceLocation biomeId, RandomSource random) {
        throw new AssertionError();
    }

    @Inject(method = "getCreativeMusic", at = @At("HEAD"), cancellable = true)
    private static void coo$creativeUsesBiomePlaylist(RandomSource random, CallbackInfoReturnable<Music> cir) {
        if (!CoOConfig.timmCreativeBiomeMusic) return;
        LocalPlayer player = Minecraft.getInstance().player;
        cir.setReturnValue(player == null ? null : player.level().getBiome(player.blockPosition()).unwrapKey()
                .map(key -> getMusicSound(key.location(), random)).orElse(null));
    }

    @Redirect(method = {"getMusicSound", "getCreativeMusic", "getMenuMusic"},
            at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;IIZ)Lnet/minecraft/sounds/Music;"))
    private static Music coo$cachedMusic(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        return CoOConfig.timmCacheMusic
                ? TimmMusicCache.get(event, minDelay, maxDelay, replaceCurrentMusic)
                : new Music(event, minDelay, maxDelay, replaceCurrentMusic);
    }
}
