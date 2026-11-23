package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DebugScreenOverlay.class)
public abstract class MixinDebugScreenTextCache {

    @Unique
    private ArrayList<String> coo$gameLines;
    @Unique
    private long coo$gameAt;
    @Unique
    private ArrayList<String> coo$systemLines;
    @Unique
    private long coo$systemAt;

    @WrapMethod(method = "getGameInformation")
    private List<String> coo$cacheGameInformation(Operation<List<String>> original) {
        int interval = CoOConfig.vanillaDebugScreenRefreshMs;
        if (interval <= 0) {
            this.coo$gameLines = null;
            return original.call();
        }
        long now = Util.getMillis();
        if (this.coo$gameLines == null || now - this.coo$gameAt >= interval || now < this.coo$gameAt) {
            this.coo$gameLines = new ArrayList<>(original.call());
            this.coo$gameAt = now;
        }
        return new ArrayList<>(this.coo$gameLines);
    }

    @WrapMethod(method = "getSystemInformation")
    private List<String> coo$cacheSystemInformation(Operation<List<String>> original) {
        int interval = CoOConfig.vanillaDebugScreenRefreshMs;
        if (interval <= 0) {
            this.coo$systemLines = null;
            return original.call();
        }
        long now = Util.getMillis();
        if (this.coo$systemLines == null || now - this.coo$systemAt >= interval || now < this.coo$systemAt) {
            this.coo$systemLines = new ArrayList<>(original.call());
            this.coo$systemAt = now;
        }
        return new ArrayList<>(this.coo$systemLines);
    }
}
