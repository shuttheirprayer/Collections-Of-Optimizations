package com.misanthropy.collections_of_optimizations.mixin.gnetum;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import com.misanthropy.collections_of_optimizations.core.GnetumCacheSettingMemo;
import me.decce.gnetum.ElementType;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.PassManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = PassManager.class, remap = false)
public abstract class MixinPassManager {

    @Overwrite(remap = false)
    public boolean cachingDisabled(String vanillaOverlay) {
        int tick = ClientTickStamp.currentOnRenderThread();
        if (!CoOConfig.gnetumMemoCacheSettings || vanillaOverlay == null || tick == -1) {
            return Gnetum.uncachedElements.has(vanillaOverlay) || !Gnetum.getCacheSetting(vanillaOverlay).enabled.get();
        }
        byte cached = GnetumCacheSettingMemo.get(0, vanillaOverlay, tick);
        if (cached != GnetumCacheSettingMemo.UNKNOWN) {
            return cached != 0;
        }
        boolean disabled = Gnetum.uncachedElements.has(vanillaOverlay) || !Gnetum.getCacheSetting(vanillaOverlay).enabled.get();
        GnetumCacheSettingMemo.record(0, vanillaOverlay, disabled);
        return disabled;
    }

    @Overwrite(remap = false)
    public boolean cachingDisabled(String moddedOverlay, ElementType type) {
        int tick = ClientTickStamp.currentOnRenderThread();
        if (!CoOConfig.gnetumMemoCacheSettings || moddedOverlay == null || type == null || tick == -1) {
            return Gnetum.uncachedElements.has(moddedOverlay, type) || !Gnetum.getCacheSetting(moddedOverlay, type).enabled.get();
        }
        int slot = type.ordinal() + 1;
        byte cached = GnetumCacheSettingMemo.get(slot, moddedOverlay, tick);
        if (cached != GnetumCacheSettingMemo.UNKNOWN) {
            return cached != 0;
        }
        boolean disabled = Gnetum.uncachedElements.has(moddedOverlay, type) || !Gnetum.getCacheSetting(moddedOverlay, type).enabled.get();
        GnetumCacheSettingMemo.record(slot, moddedOverlay, disabled);
        return disabled;
    }
}
