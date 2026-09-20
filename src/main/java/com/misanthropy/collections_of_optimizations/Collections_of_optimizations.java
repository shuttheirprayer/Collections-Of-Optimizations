package com.misanthropy.collections_of_optimizations;

import com.misanthropy.collections_of_optimizations.core.ArsElementalCurioBagKey;
import com.misanthropy.collections_of_optimizations.core.CacRedPandaTracker;
import com.misanthropy.collections_of_optimizations.core.CameraShakeScanCache;
import com.misanthropy.collections_of_optimizations.core.CoOEmbeddiumOptions;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import com.misanthropy.collections_of_optimizations.core.GhostPlayerPurge;
import com.misanthropy.collections_of_optimizations.core.GoetyDelightLogItems;
import com.misanthropy.collections_of_optimizations.core.LeakProbe;
import com.misanthropy.collections_of_optimizations.core.MahouInsightTargetPurge;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import com.misanthropy.collections_of_optimizations.core.CuriosSlotCache;
import com.misanthropy.collections_of_optimizations.core.IafDenRegistry;
import com.misanthropy.collections_of_optimizations.core.MapTintCache;
import com.misanthropy.collections_of_optimizations.core.RecipeCacheGeneration;
import com.misanthropy.collections_of_optimizations.core.SkyArenaAltarMapPurge;
import com.misanthropy.collections_of_optimizations.core.SummonityMinionCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Collections_of_optimizations.MODID)
public class Collections_of_optimizations {
    public static final String MODID = "collections_of_optimizations";

    public Collections_of_optimizations() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoOConfig.SPEC, MODID + "-common.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CoOConfig::onLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CoOConfig::onReload);

        if (ModList.get().isLoaded("bloodmagic") || ModList.get().isLoaded("celestial_core")
                || ModList.get().isLoaded("experienceobelisk") || ModList.get().isLoaded("cookingforblockheads")) {
            RecipeCacheGeneration.register();
        }

        if (ModList.get().isLoaded("goetydelight")) {
            GoetyDelightLogItems.register();
        }

        if (ModList.get().isLoaded("supplementaries")) {
            MapTintCache.register();
        }

        if (ModList.get().isLoaded("curios")) {
            CuriosSlotCache.register();
            CurioPresenceCache.register();
        }

        if (ModList.get().isLoaded("summonity")) {
            SummonityMinionCache.register();
        }

        if (ModList.get().isLoaded("crittersandcompanions")) {
            CacRedPandaTracker.register();
        }

        if (ModList.get().isLoaded("fdbosses")) {
            FdBossesPresence.register();
        }

        if (ModList.get().isLoaded("mahoutsukai")) {
            MahouInsightTargetPurge.register();
        }

        if (ModList.get().isLoaded("skyarena")) {
            SkyArenaAltarMapPurge.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("embeddium")) {
            CoOEmbeddiumOptions.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("ars_elemental")) {
            ArsElementalCurioBagKey.register();
        }

        ClientTickStamp.register();
        CameraShakeScanCache.register();

        GhostPlayerPurge.register();
        LeakProbe.register();

        IafDenRegistry.register();
    }
}
