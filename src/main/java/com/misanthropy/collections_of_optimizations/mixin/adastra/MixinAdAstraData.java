package com.misanthropy.collections_of_optimizations.mixin.adastra;

import com.google.gson.JsonElement;
import com.misanthropy.collections_of_optimizations.core.PlanetDefaultsMemo;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = AdAstraData.class, remap = false)
public abstract class MixinAdAstraData {

    @Inject(method = "apply", at = {@At("HEAD"), @At("RETURN")}, require = 0)
    private void coo$invalidateGravityMemo(Map<ResourceLocation, JsonElement> object,
                                           ResourceManager resourceManager,
                                           ProfilerFiller profiler,
                                           CallbackInfo ci) {
        PlanetDefaultsMemo.invalidate();
    }

    @Inject(method = "setPlanets", at = {@At("HEAD"), @At("RETURN")}, require = 0)
    private static void coo$invalidateGravityMemoOnSync(Map<ResourceKey<Level>, Planet> planets, CallbackInfo ci) {
        PlanetDefaultsMemo.invalidate();
    }
}
