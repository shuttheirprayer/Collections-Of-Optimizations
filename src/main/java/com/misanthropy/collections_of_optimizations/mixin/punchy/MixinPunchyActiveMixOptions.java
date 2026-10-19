package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PunchyMemos;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import punchy.config.PunchyTuningConfig;

import java.util.List;
import java.util.Set;

@Mixin(value = PunchyTuningConfig.class, remap = false)
public abstract class MixinPunchyActiveMixOptions {

    @Shadow
    private static List<String> cachedSelectedResourcePackIdsHighToLow;

    @Shadow
    private static ResourceManager cachedSelectedResourcePackResourceManager;

    @WrapMethod(method = "getActiveMixPackOptionIds", require = 0)
    private static Set<String> coo$memoActiveOptions(Operation<Set<String>> original) {
        if (!CoOConfig.punchyMemoActiveMixOptions) {
            return original.call();
        }
        List<String> selected = cachedSelectedResourcePackIdsHighToLow;
        Minecraft client = Minecraft.getInstance();
        if (selected == null || client == null || cachedSelectedResourcePackResourceManager != client.getResourceManager()) {
            return original.call();
        }
        Set<String> known = PunchyMemos.activeOptions(selected);
        if (known != null) {
            return known;
        }
        Set<String> built = original.call();
        PunchyMemos.rememberActiveOptions(selected, built);
        return built;
    }
}
