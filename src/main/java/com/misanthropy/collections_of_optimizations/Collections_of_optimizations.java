package com.misanthropy.collections_of_optimizations;

import com.misanthropy.collections_of_optimizations.core.*;
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
        NbtFastLoad.logState();

        if (ModList.get().isLoaded("bloodmagic") || ModList.get().isLoaded("celestial_core")
                || ModList.get().isLoaded("experienceobelisk") || ModList.get().isLoaded("cookingforblockheads")) {
            RecipeCacheGeneration.register();
        }

        if (ModList.get().isLoaded("goetydelight")) {
            GoetyDelightLogItems.register();
        }

        if (ModList.get().isLoaded("create")) {
            CreateRuntimeDataCache.register();
        }

        if (ModList.get().isLoaded("supplementaries")) {
            MapTintCache.register();
        }

        if (ModList.get().isLoaded("alltheleaks")) {
            LeakTrackerPruner.register();
        }

        if (ModList.get().isLoaded("elysium_api")) {
            ElysiumSoundTransformers.register();
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

        if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
            GeomancyGrandmasterTracker.register();
        }

        if (ModList.get().isLoaded("ars_additions")) {
            ArsAdditionsPortalScanCache.register();
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

        StalePlayerMaps.register();

        if (ModList.get().isLoaded("simply_swords_overhaul")) {
            SsoDashGuard.register();
        }

        if (ModList.get().isLoaded("l2library")) {
            L2LibraryTrackedEffects.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("embeddium")) {
            CoOEmbeddiumOptions.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("ars_elemental")) {
            ArsElementalCurioBagKey.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("entity_model_features") && ModList.get().isLoaded("supplementaries")) {
            EmfStatuePause.register();
        }

        ClientTickStamp.register();
        CameraShakeScanCache.register();
        ClientLevelRelease.register();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            WarmClasses.register();
        }
        VehicleCount.register();

        GhostPlayerPurge.register();
        LeakProbe.register();

        IafDenRegistry.register();
    }
}
