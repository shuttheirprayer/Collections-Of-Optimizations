package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

import java.util.IdentityHashMap;
import java.util.Map;

public final class TimmMusicCache {

    private static final Map<Holder<SoundEvent>, Music> CACHE = new IdentityHashMap<>();

    private TimmMusicCache() {
    }

    public static Music get(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        Music music = CACHE.get(event);
        if (music == null || music.getMinDelay() != minDelay || music.getMaxDelay() != maxDelay
                || music.replaceCurrentMusic() != replaceCurrentMusic) {
            music = new Music(event, minDelay, maxDelay, replaceCurrentMusic);
            CACHE.put(event, music);
        }
        return music;
    }
}
