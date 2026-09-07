package com.misanthropy.collections_of_optimizations;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

public final class CoOConfig {

    public static final ForgeConfigSpec SPEC;

    private static final List<Runnable> BAKERS = new ArrayList<>();

    private static ForgeConfigSpec.BooleanValue fastBiomeBlendValue;

    public static boolean masterEnabled = true;

    public static boolean curiosSkipSlotlessEntities = true;
    public static boolean curiosSkipClientTickOnNonPlayers = true;
    public static boolean curiosSkipNonPlayerRenderLayer = true;
    public static boolean curiosCacheEntitySlotLookup = true;
    public static boolean curiosFastEquippedItemMiss = true;
    public static boolean curiosFastFindFirstMiss = true;
    public static boolean curiosReuseCurioMapView = true;

    public static boolean artifactsSkipClientTickOnNonPlayers = true;
    public static boolean artifactsFastPathKittySlippers = true;
    public static boolean artifactsFastPathUmbrella = true;

    public static boolean caelusSkipGroundedNonPlayers = true;

    public static boolean blockswapPaletteFilteredRetroGen = true;
    public static boolean justdirethingsAvoidChunkTickets = true;
    public static boolean justdirethingsLeanAreaPreviewScan = true;
    public static int goetydelightCakeScanInterval = 4;
    public static boolean goetydelightSkipIdleVisualEffects = true;
    public static boolean goetydelightLeanLogScan = true;
    public static boolean goetydelightCacheLogItems = true;

    public static boolean regionsunexploredCacheFurnaceBurnTimes = true;

    public static boolean gnetumSingleModIdLookup = true;

    public static boolean endinglibraryLeanCameraCapLookup = true;

    public static boolean bettercombatCacheWeaponAttributes = true;

    public static boolean cofhCacheTranslucentRenderers = true;

    public static boolean createDedupeBigOutlineProbes = true;
    public static int createBigOutlinePickInterval = 5;

    public static boolean xaerolibCacheConfigProfile = true;
    public static boolean xaerolibCacheEnforcementCheck = true;

    public static int xaeroworldmapVramPollInterval = 500;

    public static boolean geckolibReuseRenderVectors = true;
    public static boolean geckolibCacheBoneLookup = true;

    public static boolean saintsdragonsSkipRedundantBoneTracking = true;
    public static boolean saintsdragonsCacheShakeScan = true;

    public static boolean immediatelyfastSingleBufferLookup = true;
    public static boolean immediatelyfastSkipIdleLayers = true;

    public static boolean modernfixFastRepresentedTabs = true;

    public static int fancymenuSeamlessCaptureInterval = 30;
    public static boolean fancymenuSkipRedundantScaleWrites = true;
    public static boolean fancymenuPinRenderStateToRenderThread = true;
    public static boolean fancymenuCacheScreenIdentifiers = true;

    public static boolean emfDropZeroAngerEntries = true;

    public static boolean etfFastValidPath = true;

    public static boolean oculusSkipSignTextInShadowPass = true;
    public static boolean oculusSkipGlintInShadowPass = true;
    public static boolean oculusSkipNameTagsInShadowPass = true;
    public static boolean oculusSkipBannerPatternsInShadowPass = true;
    public static boolean oculusSkipBatchingWithoutShaders = true;

    public static boolean vanillaCacheArmorModelBakes = true;
    public static boolean vanillaCacheOutlineEdges = true;

    public static boolean lootrSkipIdleTileTicker = true;
    public static int lootrTileTickerBudget = 512;

    public static boolean naturesauraFastAuraChunkSweep = true;

    public static int xaeroMinimapRenderFpsCap = 0;
    public static boolean w2w2DeferWaypointSave = true;

    public static boolean terrablenderCacheNamespaceRule = true;

    public static boolean biomeswevegoneSkipForeignChunkTerrain = true;

    public static boolean biolithRestampSwappedBiomeSource = true;
    public static boolean biolithEarlyRegistryCapture = true;
    public static boolean biolithReuseBiomeEntries = true;

    public static boolean terramitySkipItemAnimationCopies = true;
    public static boolean terramitySkipForeignEntityAnimations = true;
    public static boolean terramityMemoizeProcedureRaycasts = true;
    public static boolean terramitySkipClientCurioScans = true;
    public static boolean terramitySkipArmorAnimationScan = true;
    public static boolean terramityFixPhasingShaderStomp = true;

    public static boolean armageddonSkipForeignEntityAnimations = true;
    public static boolean armageddonCacheProgressionIds = true;

    public static boolean borninchaosSkipItemAnimationCopies = true;
    public static boolean borninchaosSkipForeignEntityAnimations = true;
    public static boolean borninchaosSkipRedundantDimensionRefresh = true;
    public static boolean borninchaosNarrowMinionScans = true;
    public static boolean cncSkipClientEncounterScans = true;
    public static int cncEncounterScanInterval = 5;
    public static boolean cncSkipForeignEntityAnimations = true;
    public static boolean cncSkipRedundantDimensionRefresh = true;
    public static boolean cncGateCaribouDashScan = true;
    public static boolean cncCacheWechugeFogScan = true;
    public static int cncSasquatchTextureScanInterval = 4;
    public static boolean cncSkipClientWoodKnockScan = true;
    public static boolean cncFixSobbingShaderStomp = true;

    public static boolean crittersandcompanionsGateRedPandaAvoidGoal = true;
    public static int crittersandcompanionsKoiLuckScanInterval = 10;
    public static boolean crittersandcompanionsMemoNecklaceLookup = true;
    public static boolean crittersandcompanionsSkipEmptyLeashRender = true;
    public static int crittersandcompanionsCritterItemScanInterval = 4;
    public static boolean crittersandcompanionsFastBehaviourLookup = true;

    public static boolean cucumberLeanTileDispatch = true;
    public static boolean cucumberLeanTagTooltip = true;
    public static boolean cucumberCacheTagLookup = true;

    public static boolean dynamictreesLeanLeafPlacement = true;
    public static boolean dynamictreesLeanLeafHydration = true;
    public static boolean dynamictreesCacheThickTrunkShape = true;

    public static boolean mysticalagricultureLeanAugmentLookup = true;
    public static boolean mysticalagricultureLeanAbilityCache = true;
    public static boolean mysticalagricultureSkipIdleSoulExtractor = true;
    public static boolean mysticalagricultureMemoCropNameKey = true;

    public static boolean skarriermobsSkipForeignEntityAnimations = true;
    public static boolean skarriermobsLeanDaylightBurnScan = true;
    public static boolean skarriermobsLeanResisteelToolScan = true;
    public static boolean skarriermobsLeanResisteelSwordScan = true;
    public static boolean skarriermobsLeanResisteelSetTracking = true;
    public static boolean skarriermobsLeanTargetProximityScans = true;
    public static boolean skarriermobsNarrowRegionScans = true;
    public static boolean industrialforegoingSkipStasisTagChurn = true;
    public static boolean enigmaticaddonsSkipUnsetPersistentData = true;
    public static boolean enigmaticaddonsSkipIdleFrostScan = true;
    public static boolean enigmaticdelicacySkipUnsetPersistentData = true;
    public static int enigmaticlegacyGuardianHeartScanInterval = 4;
    public static boolean enigmaticlegacyNarrowCursedRingAngerScan = true;
    public static boolean enigmaticlegacyPruneAngeredGuardians = true;
    public static boolean travelopticsLeanClimbCurioScan = true;
    public static boolean travelopticsLeanCastEffectChecks = true;
    public static boolean celestialenchantmentsSkipUnenchantedTick = true;
    public static boolean celestialenchantmentsLeanSlotEnchScan = true;
    public static boolean uniqueaccessoriesLeanWaistWarmerScan = true;
    public static boolean uniqueaccessoriesSkipUnsetPersistentData = true;

    public static boolean bloodmagicCacheArcRecipeList = true;
    public static boolean bloodmagicCacheArcFurnaceRecipe = true;
    public static boolean bloodmagicFastRoutingConnectivity = true;

    public static boolean animusCacheEquivalencyPreview = true;

    public static boolean patchouliCacheBookItemLookup = true;

    public static boolean structurifyFastStructureSetLookup = true;
    public static boolean structurifySkipDisabledStructureChecks = true;
    public static boolean structurifyLeanHeightCache = true;
    public static boolean structurifyLeanOverlapSections = true;
    public static boolean structurifyCacheStructureSetEntries = true;
    public static boolean structurifySkipStartCheckWrap = true;

    public static boolean bossesriseNarrowCinematicScan = true;
    public static boolean bossesriseLeanVfxScan = true;
    public static boolean soulsweaponsLeanDespawnTimer = true;
    public static boolean konweaponSkipItemAnimationCopies = true;
    public static boolean immersiveaircraftBatchOverlay = true;
    public static boolean ftbchunksSkipHiddenMinimapWork = true;
    public static boolean ftbchunksFastRegionWrite = true;
    public static boolean ftbchunksMemoMinimapRegions = true;
    public static boolean punchyCacheResourceStackMisses = true;
    public static boolean l2hostilitySkipTraitlessCapLookup = true;
    public static boolean iceandfireFastEntityDataLookup = true;
    public static boolean iceandfireSkipPathDebugRender = true;
    public static boolean iceandfireSkipEmptyArmorLayer = true;
    public static boolean iceandfireCacheDragonTexture = true;
    public static boolean iceandfireSkipEmptyDragonLayers = true;
    public static boolean iceandfireLeanMultipartTick = true;
    public static int iceandfireDragonTargetSearchHeight = 32;
    public static boolean iafdragonfixStructureDens = true;
    public static int iafdragonfixRoostSpawnDistance = 800;
    public static int iafdragonfixCaveSpawnDistance = 800;
    public static boolean mowziesmobsFastCapabilityLookup = true;
    public static boolean mowziesmobsDedupeCapabilityAttach = true;
    public static boolean mowziesmobsCacheCameraShakeScan = true;
    public static int mowziesmobsBossMusicPacketInterval = 5;
    public static boolean mowziesmobsLeanBoneLookup = true;
    public static boolean mowziesmobsHoistChainRenderMatrix = true;
    public static int mowziesmobsDynamicChainSubstepCap = 4;
    public static boolean mowziesmobsCacheUmvuthanaLeader = true;
    public static boolean mowziesmobsLeanModelBoxVectors = true;
    public static boolean mowziesmobsSkipBlankElokosaTransform = true;
    public static boolean mowziesmobsLeanLayerBoneScan = true;
    public static boolean mowziesmobsCacheEffectRenderTypes = true;
    public static boolean pickupnotifierSkipOpaqueSpriteBuffer = true;
    public static boolean placeboSkipEmptyEnchantmentEvent = true;

    public static boolean photonLeanParticleQuads = true;
    public static boolean photonLeanParticleLight = true;
    public static boolean photonLeanTrailVertices = true;
    public static boolean photonDropEmptyEffectCacheEntries = true;
    public static boolean integratedapiSkipEmptyBeardifier = true;
    public static boolean echelonCacheTierAttributeUuids = true;
    public static boolean elysiumapiMemoClimateSample = true;
    public static boolean elysiumapiSkipUnusedBiomeReplacerLookup = true;
    public static boolean enigmaticdiceFastCurioMiss = true;
    public static boolean balmMemoDynamicModelKeys = true;

    public static boolean dungeoncrawlSkipBlockEntityProbe = true;

    public static boolean moonlightSkipEmptyMapMarkerScan = true;

    public static boolean pehkuiLeanScaleTick = true;
    public static boolean pehkuiMemoModifierType = true;

    public static boolean tonsofenchantsSkipAbsentAttributeRemoval = true;
    public static boolean tonsofenchantsFrostbiteSkipClient = true;
    public static boolean tonsofenchantsLeanAttributeLookup = true;
    public static boolean tonsofenchantsSinglePhasePlayerTick = true;

    public static boolean subtleeffectsFireflyDarknessGate = true;
    public static boolean subtleeffectsCapBiomeParticleScan = true;
    public static boolean subtleeffectsLeanTickerRemoval = true;
    public static boolean subtleeffectsGeyserBlockPreFilter = true;

    public static boolean arsengSkipDeadRelayListeners = true;
    public static boolean arsengGateGenericInvWrapper = true;

    public static boolean perceptionShareDefaultTrailData = true;

    public static boolean quarkSkipPigLitterTagChurn = true;

    public static boolean zetaLeanStructureReplacement = true;
    public static boolean zetaShareEventWrappers = true;

    public static boolean goetyCacheCapabilityOptional = true;
    public static boolean goetySkipCapabilityFallback = true;
    public static boolean goetyMemoAttributeModifiers = true;
    public static boolean goetyFastEmptyAllyCheck = true;
    public static boolean goetyFastCurioItemMiss = true;
    public static boolean goetyMemoCurioFilter = true;
    public static boolean goetySkipBossMusicTargetLookup = true;
    public static boolean goetyCacheFogWightScan = true;
    public static boolean goetyCacheShakeScan = true;

    public static boolean cataclysmCacheShakeScan = true;
    public static boolean dodosmobsCacheShakeScan = true;
    public static boolean eeeabsmobsCacheShakeScan = true;
    public static boolean fromtheshadowsCacheShakeScan = true;
    public static boolean gtbcsCacheShakeScan = true;
    public static boolean legendarymonstersCacheShakeScan = true;

    public static boolean mythsandlegendsCacheFogBossScan = true;
    public static boolean mythsandlegendsCacheShakeScan = true;

    public static boolean ambientsoundsMemoBiomeMatch = true;
    public static int arsnouveauSkyTextureInterval = 1;
    public static boolean pehkuiMemoInteractionBoxScales = true;
    public static boolean pehkuiCacheClientScales = true;

    public static boolean relicsClampEssenceSpeed = true;
    public static boolean morerelicsHoistEquippedCurios = true;
    public static boolean terracurioCachedCurioLookup = true;
    public static boolean terracurioLeanAttributeMap = true;
    public static boolean terracurioSkipIdleAggroScan = true;
    public static boolean terracurioSkipUnchangedIceFlag = true;
    public static boolean cosmeticarmorPerPlayerRestoreQueue = true;
    public static double relicsEssenceMaxSpeed = 4.0D;

    public static boolean morehitboxesSkipAbsentMultiPartFilter = true;

    public static boolean goetyrevelationCacheHaloLookup = true;
    public static boolean revelationfixSkipMobFluidStandScan = true;
    public static boolean revelationfixSkipNonSpiderHurtByTargetEvents = true;

    public static boolean macabreSkipForeignEntityAnimations = true;
    public static boolean macabreSkipItemAnimationCopies = true;
    public static boolean macabreCoalesceVariableSync = true;

    public static boolean alexsmobsSkipCreeperAvoidGoals = true;
    public static int alexsmobsSpiderFlyScanInterval = 10;
    public static boolean alexsmobsReleaseLevelMaps = true;

    public static boolean alexscavesMemoRareBiomeQuads = true;
    public static boolean alexscavesMemoClimateSample = true;
    public static boolean alexscavesCacheShakeScan = true;

    public static boolean adastraMemoPlanetDefaults = true;

    public static boolean supplementariesLeanEndermanSkullWatch = true;
    public static boolean supplementariesSkipNonSignCapSync = true;
    public static boolean supplementariesMemoMapTintLookup = true;

    public static boolean amendmentsSkipIdleSwaySync = true;

    public static boolean copycatsMemoStateOcclusion = true;
    public static boolean copycatsFastMigrationChecks = true;
    public static boolean copycatsCachedModelConfig = true;
    public static boolean copycatsLeanVirtualWorldCheck = true;

    public static int itemEntityRenderCap = 1;
    public static boolean vanillaMemoGlyphFontSet = true;
    public static boolean vanillaFasterStructureLocation = true;
    public static boolean vanillaFixBoatFallDamage = false;
    public static boolean vanillaPredictableItemDrops = false;
    public static boolean vanillaLeanTrackerSectionPos = true;
    public static boolean vanillaLeanSuffocationScan = true;
    public static boolean vanillaLeanMenuBroadcast = true;
    public static boolean vanillaLeanTrackerDelta = true;
    public static boolean vanillaCacheBiomeQuartLookups = true;
    public static boolean vanillaMemoCameraFluid = true;
    public static boolean vanillaMemoSkyColour = true;
    public static boolean vanillaPurgeGhostPlayers = true;
    public static boolean vanillaFastBiomeBlend = true;
    public static double vanillaMovementCheckSlack = 100.0D;
    public static boolean vanillaDisableFlyingKick = true;
    public static boolean vanillaCacheModelPartLookups = true;
    public static boolean vanillaLeanKeyframeAnimation = true;
    public static boolean createsolarMemoGogglesLookup = true;
    public static boolean xaeroworldmapIdleMapFrameWait = true;
    public static int xaeroworldmapMapFrameSpinTail = 200;
    public static boolean gnetumMemoCacheSettings = true;
    public static boolean mcreatorShareDefaultPlayerVariables = true;

    public static boolean distanthorizonsClearBiomeCachesOnUnload = true;
    public static boolean distanthorizonsMemoBiomeBlendColors = true;
    public static boolean distanthorizonsCacheChunkBiomeLookup = true;

    public static boolean summonityCacheOwnedMinionScans = true;
    public static int summonityIdleTargetScanInterval = 4;
    public static boolean summonityCacheDragonParts = true;
    public static boolean summonityFastCurioMiss = true;
    public static boolean summonitySkipUnchangedSlotSync = true;
    public static boolean summonityMemoDroneModifierIds = true;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        define(builder);
        SPEC = builder.build();
    }

    private CoOConfig() {
    }

    @FunctionalInterface
    private interface BoolSink {
        void accept(boolean value);
    }

    private static ForgeConfigSpec.BooleanValue gate(ForgeConfigSpec.BooleanValue value, BoolSink sink) {
        BAKERS.add(() -> sink.accept(masterEnabled && value.get()));
        return value;
    }

    private static void gate(ForgeConfigSpec.IntValue value, int disabled, IntConsumer sink) {
        BAKERS.add(() -> sink.accept(masterEnabled ? value.get() : disabled));
    }

    private static void gate(ForgeConfigSpec.DoubleValue value, double disabled, DoubleConsumer sink) {
        BAKERS.add(() -> sink.accept(masterEnabled ? value.get() : disabled));
    }

    private static void plain(ForgeConfigSpec.IntValue value, IntConsumer sink) {
        BAKERS.add(() -> sink.accept(value.get()));
    }

    private static void plain(ForgeConfigSpec.DoubleValue value, DoubleConsumer sink) {
        BAKERS.add(() -> sink.accept(value.get()));
    }

    private static void define(ForgeConfigSpec.Builder builder) {
        builder.comment("Master switch for every patch in this mod. Useful for A/B testing.").push("general");
        ForgeConfigSpec.BooleanValue master = builder.define("enabled", true);
        BAKERS.add(() -> masterEnabled = master.get());
        builder.pop();

        builder.comment("Curios API patches.").push("curios");
        gate(builder
                .comment("Skip the per-tick curios handler for entity types that have no curio slots at all.")
                .define("skipSlotlessEntities", true), v -> curiosSkipSlotlessEntities = v);
        gate(builder
                .comment("Skip the client-side curios tick for non-player entities.")
                .define("skipClientTickOnNonPlayers", true), v -> curiosSkipClientTickOnNonPlayers = v);
        gate(builder
                .comment("Never attach the Curios render layer to non-player entity renderers.")
                .define("skipNonPlayerRenderLayer", true), v -> curiosSkipNonPlayerRenderLayer = v);
        gate(builder
                .comment("Cache the per-entity curio slot lookup on the entity itself.")
                .define("cacheEntitySlotLookup", true), v -> curiosCacheEntitySlotLookup = v);
        gate(builder
                .comment("Answer 'this entity is not wearing that item' from a per-tick set instead of a full curios inventory walk.")
                .define("fastEquippedItemMiss", true), v -> curiosFastEquippedItemMiss = v);
        gate(builder
                .comment("Answer ICuriosItemHandler#findFirstCurio(Item) from the same per-tick set instead of walking every slot handler.")
                .define("fastFindFirstMiss", true), v -> curiosFastFindFirstMiss = v);
        gate(builder
                .comment("Hand out one reusable unmodifiable view of an entity's curio slot map instead of wrapping the map again on every single call.")
                .define("reuseCurioMapView", true), v -> curiosReuseCurioMapView = v);
        builder.pop();

        builder.comment("Artifacts patches.").push("artifacts");
        gate(builder
                .comment("Skip the client-side Artifacts living tick for non-player entities.")
                .define("skipClientTickOnNonPlayers", true), v -> artifactsSkipClientTickOnNonPlayers = v);
        gate(builder
                .comment("Skip the kitty slippers curios scan when the entity has no last-hurt-by mob.")
                .define("fastPathKittySlippers", true), v -> artifactsFastPathKittySlippers = v);
        gate(builder
                .comment("Skip the charm of sinking curios scan when the umbrella glide check cannot pass anyway.")
                .define("fastPathUmbrella", true), v -> artifactsFastPathUmbrella = v);
        builder.pop();

        builder.comment("Caelus patches.").push("caelus");
        gate(builder
                .comment("Skip the flight attribute lookup for non-player entities that are not already fall-flying.")
                .define("skipGroundedNonPlayers", true), v -> caelusSkipGroundedNonPlayers = v);
        builder.pop();

        builder.comment("Block Swap patches.").push("blockswap");
        gate(builder
                .comment("Filter the retro-gen chunk sweep through each section's block state palette.")
                .define("paletteFilteredRetroGen", true), v -> blockswapPaletteFilteredRetroGen = v);
        builder.pop();

        builder.comment("Just Dire Things patches.").push("justdirethings");
        gate(builder
                .comment("Read the item entity's block state through getChunkNow instead of the loading getChunk.")
                .define("avoidChunkTickets", true), v -> justdirethingsAvoidChunkTickets = v);
        gate(builder
                .comment("Stop the area preview renderer copying every block entity of all 169 nearby chunks into a fresh list every frame, and skip the whole 169 chunk sweep for the rest of a tick once that tick's first frame found no area affecting block at all. A preview switched on part way through a tick shows up on the next one.")
                .define("leanAreaPreviewScan", true), v -> justdirethingsLeanAreaPreviewScan = v);
        builder.pop();

        builder.comment("Goety's Delight patches.").push("goetydelight");
        gate(builder
                .comment("Run the cherry blossom cake entity sweep once every N server ticks instead of every tick.")
                .defineInRange("cakeScanInterval", 4, 1, 100), 1, v -> goetydelightCakeScanInterval = v);
        gate(builder
                .comment("Skip the per-frame walk over every entity in the level while no entity carries a visual effect. The walk re-arms as soon as one is added or synced.")
                .define("skipIdleVisualEffects", true), v -> goetydelightSkipIdleVisualEffects = v);
        gate(builder
                .comment("Test the log tag against the item itself instead of building a throwaway ItemStack for every item in the registry on every boat plate result lookup.")
                .define("leanLogScan", true), v -> goetydelightLeanLogScan = v);
        gate(builder
                .comment("Keep the boat plate log list instead of rescanning the whole item registry on every result lookup. Rebuilt whenever tags reload.")
                .define("cacheLogItems", true), v -> goetydelightCacheLogItems = v);
        builder.pop();

        builder.comment("Better Combat patches.").push("bettercombat");
        gate(builder
                .comment("Cache WeaponRegistry#getAttributes per item. Stock does a registry reverse lookup plus a map get on every Player#getItemBySlot call, twice.")
                .define("cacheWeaponAttributes", true), v -> bettercombatCacheWeaponAttributes = v);
        builder.pop();

        builder.comment("CoFH Core patches.").push("cofh");
        gate(builder
                .comment("Remember which entity classes have a translucent renderer so the per-frame entity walk skips the renderer lookup for the rest.")
                .define("cacheTranslucentRenderers", true), v -> cofhCacheTranslucentRenderers = v);
        builder.pop();

        builder.comment("Create patches.").push("create");
        gate(builder
                .comment("Read each block position at most once per big outline pick. Stock tests a 3x3x3 neighbourhood at every raycast step, so neighbouring steps re-read the same positions.")
                .define("dedupeBigOutlineProbes", true), v -> createDedupeBigOutlineProbes = v);
        gate(builder
                .comment("Shortest gap, in milliseconds, between Create's big outline raycasts. Create casts a fresh ray from your eye to your reach limit on every frame drawn, so above a hundred frames a second it casts far more often than the outline can visibly change. Skipped frames keep the previous target, which also feeds block breaking and placing, so a value here is how stale that target may get. Vanilla only recalculates it every 50. 0 casts on every frame.")
                .defineInRange("bigOutlinePickInterval", 5, 0, 50), 0, v -> createBigOutlinePickInterval = v);
        builder.pop();

        builder.comment("XaeroLib patches.").push("xaerolib");
        gate(builder
                .comment("Resolve the active config profile once per client tick instead of on every single config read.")
                .define("cacheConfigProfile", true), v -> xaerolibCacheConfigProfile = v);
        gate(builder
                .comment("Answer the server-enforcement check once per client tick. Stock runs a second full config read inside every config read.")
                .define("cacheEnforcementCheck", true), v -> xaerolibCacheEnforcementCheck = v);
        builder.pop();

        builder.comment("Xaero's World Map patches.").push("xaeroworldmap");
        gate(builder
                .comment("Milliseconds between the map limiter's free VRAM query, which stock fires a blocking glGetIntegerv for on every single frame. 0 restores the stock every frame behaviour. Client.")
                .defineInRange("vramPollInterval", 500, 0, 60000), 0, v -> xaeroworldmapVramPollInterval = v);
        gate(builder
                .comment("Wait out the world map screen's frame budget by parking instead of spinning. While the map screen is open Xaero busy loops on System.nanoTime until 1.6ms of the frame has passed, which burns a whole core for most of every map frame. Pacing is unchanged, the thread just idles instead.")
                .define("idleMapFrameWait", true), v -> xaeroworldmapIdleMapFrameWait = v);
        gate(builder
                .comment("Microseconds of the world map frame budget still spent spinning, so the wait ends on time even though the operating system wakes a parked thread late. Raise it if the map screen feels choppier than before, lower it to give the core back sooner.")
                .defineInRange("mapFrameSpinTail", 200, 0, 1600), 1600, v -> xaeroworldmapMapFrameSpinTail = v);
        builder.pop();

        builder.comment("GeckoLib patches.").push("geckolib");
        gate(builder
                .comment("Reuse the scratch vectors GeckoLib allocates while writing model geometry.")
                .define("reuseRenderVectors", true), v -> geckolibReuseRenderVectors = v);
        gate(builder
                .comment("Memoise BakedGeoModel#getBone(String) per baked model.")
                .define("cacheBoneLookup", true), v -> geckolibCacheBoneLookup = v);
        builder.pop();

        builder.comment("Saint's Dragons patches.").push("saintsdragons");
        gate(builder
                .comment("Enable matrix tracking on a dragon's bones once per model instead of every pass.")
                .define("skipRedundantBoneTracking", true), v -> saintsdragonsSkipRedundantBoneTracking = v);
        gate(builder
                .comment("Look for screen shaking dragons once per tick instead of once per frame.")
                .define("cacheShakeScan", true), v -> saintsdragonsCacheShakeScan = v);
        builder.pop();

        builder.comment("ImmediatelyFast patches.").push("immediatelyfast");
        gate(builder
                .comment("Find a render layer's buffer with one map lookup instead of a contains check plus a get.")
                .define("singleBufferLookup", true), v -> immediatelyfastSingleBufferLookup = v);
        gate(builder
                .comment("End only the render layers that were actually drawn into instead of walking every fixed buffer on each flush.")
                .define("skipIdleLayers", true), v -> immediatelyfastSkipIdleLayers = v);
        builder.pop();

        builder.comment("ModernFix patches.").push("modernfix");
        gate(builder
                .comment("Work out which creative tabs JEI covers by indexing its item list once, instead of asking every tab whether it holds every item. Stock compares each item against each tab, and each comparison hashes the item's NBT, so the first creative search freezes for as long as it takes. Client.")
                .define("fastRepresentedTabs", true), v -> modernfixFastRepresentedTabs = v);
        builder.pop();

        builder.comment("FancyMenu patches.").push("fancymenu");
        gate(builder
                .comment("Seconds between seamless world loading screenshots. FancyMenu reads back the whole framebuffer each time, which stalls the render thread. Stock behaviour is 1.")
                .defineInRange("seamlessCaptureInterval", 30, 1, 600), 1, v -> fancymenuSeamlessCaptureInterval = v);
        gate(builder
                .comment("Skip FancyMenu's ThreadLocal render scale write when the value is unchanged. Stock writes it on every PoseStack push, pop and scale.")
                .define("skipRedundantScaleWrites", true), v -> fancymenuSkipRedundantScaleWrites = v);
        gate(builder
                .comment("Hold FancyMenu's render scale, translation and rotation in plain fields for the render thread instead of in ThreadLocals. Stock reads or writes three ThreadLocals on every PoseStack push, pop, scale, translate and mulPose, which the profiler puts at 2.6 percent of the client thread. Other threads keep the stock ThreadLocal. Client.")
                .define("pinRenderStateToRenderThread", true), v -> fancymenuPinRenderStateToRenderThread = v);
        gate(builder
                .comment("Remember which screen identifier belongs to which screen instead of working it out again on every render call. FancyMenu asks four times a frame, and answering walks a hash map, scans its whole universal identifier table for a matching value, and calls Class.forName. The cache is dropped whenever FancyMenu reloads or a new universal identifier is registered. Client.")
                .define("cacheScreenIdentifiers", true), v -> fancymenuCacheScreenIdentifiers = v);
        builder.pop();

        builder.comment("Entity Model Features patches.").push("emf");
        gate(builder
                .comment("Stop the anger time map growing one entry per neutral mob ever rendered.")
                .define("dropZeroAngerEntries", true), v -> emfDropZeroAngerEntries = v);
        builder.pop();

        builder.comment("Entity Texture Features patches.").push("etf");
        gate(builder
                .comment("Answer ResourceLocation#isValidPath at HEAD when the path is valid.")
                .define("fastValidPath", true), v -> etfFastValidPath = v);
        builder.pop();

        builder.comment("Oculus / Iris shadow pass patches.").push("oculus");
        gate(builder
                .comment("Skip sign text while rendering the shadow map.")
                .define("skipSignTextInShadowPass", true), v -> oculusSkipSignTextInShadowPass = v);
        gate(builder
                .comment("Report items as having no enchantment glint while rendering the shadow map.")
                .define("skipGlintInShadowPass", true), v -> oculusSkipGlintInShadowPass = v);
        gate(builder
                .comment("Skip entity name tags while rendering the shadow map.")
                .define("skipNameTagsInShadowPass", true), v -> oculusSkipNameTagsInShadowPass = v);
        gate(builder
                .comment("Skip banner pattern layers while rendering the shadow map, keeping the base cloth.")
                .define("skipBannerPatternsInShadowPass", true), v -> oculusSkipBannerPatternsInShadowPass = v);
        gate(builder
                .comment("Use the vanilla entity buffer source while no shader pack is loaded. Oculus installs its batched entity renderer on every level render whether or not shaders are on, and that renderer rebuilds a translucency graph per frame. With shaders off the profiler puts it at 1.6 percent of the render thread for no visual difference. Turning a shader pack on restores the batched path. Client.")
                .define("skipBatchingWithoutShaders", true), v -> oculusSkipBatchingWithoutShaders = v);
        builder.pop();

        builder.comment("Armour model patches.").push("armor");
        gate(builder
                .comment("Reuse the baked model parts an armour item builds for itself instead of baking them again on every call. Eighty six armour items in this pack build a whole model from scratch each time the game asks what model they use, and asking happens several times a frame per worn piece. With asynchronous model loading installed each of those bakes parks the render thread until a worker finishes. The reuse is limited to models built inside an armour model lookup and is dropped on a resource reload. Client.")
                .define("cacheArmorModelBakes", true), v -> vanillaCacheArmorModelBakes = v);
        gate(builder
                .comment("Remember the edge list of a block outline shape instead of walking the shape again every frame. The walk is the same work for the same block and the game repeats it once per frame for whatever you are looking at, which at a few hundred frames a second is a few hundred identical walks a second. The drawing itself is unchanged. Client.")
                .define("cacheOutlineEdges", true), v -> vanillaCacheOutlineEdges = v);
        builder.pop();

        builder.comment("Lootr patches.").push("lootr");
        gate(builder
                .comment("Skip Lootr's per-server-tick container conversion pass while its queues are empty.")
                .define("skipIdleTileTicker", true), v -> lootrSkipIdleTileTicker = v);
        gate(builder
                .comment("Maximum container conversion candidates Lootr may examine per server tick.")
                .defineInRange("tileTickerBudget", 512, 0, 65536), 0, v -> lootrTileTickerBudget = v);
        builder.pop();

        builder.comment("Nature's Aura patches.").push("naturesaura");
        gate(builder
                .comment("Keep each chunk's Nature's Aura capability handle instead of walking the capability dispatcher once per loaded chunk per second.")
                .define("fastAuraChunkSweep", true), v -> naturesauraFastAuraChunkSweep = v);
        builder.pop();

        builder.comment("Xaero's Minimap patches.").push("xaerominimap");
        gate(builder
                .comment("Maximum times per second the minimap redraws its map contents. Anything below the client frame rate leaves a visibly stale minimap, so this is off by default. 0 restores the stock every frame behaviour. Client.")
                .defineInRange("renderFpsCap", 0, 0, 260), 0, v -> xaeroMinimapRenderFpsCap = v);
        builder.pop();

        builder.comment("Xaero's + Waystones compatibility patches.").push("w2w2");
        gate(builder
                .comment("Collapse the waypoint file writes this mod does when waystone data arrives.")
                .define("deferWaypointSave", true), v -> w2w2DeferWaypointSave = v);
        builder.pop();

        builder.comment("Oh The Biomes We've Gone patches.").push("biomeswevegone");
        gate(builder
                .comment("Skip the Crag Gardens and Basalt Barrera terrain passes in chunks that contain neither biome. Both passes run on every chunk generated in every dimension and build four noise generators, two weighted state providers and 512 biome lookups before they ever check whether the biome is present.")
                .define("skipForeignChunkTerrain", true), v -> biomeswevegoneSkipForeignChunkTerrain = v);
        builder.pop();

        builder.comment("Biolith patches.").push("biolith");
        gate(builder
                .comment("Re-tag the biome source with its dimension after another mod swaps it. MCreator biome mods replace the overworld MultiNoiseBiomeSource on ServerAboutToStart, and the replacement never passes through LevelStem, so Biolith loses track of which dimension it is and silently stops applying every biome replacement and sub-biome it was asked for.")
                .define("restampSwappedBiomeSource", true), v -> biolithRestampSwappedBiomeSource = v);
        gate(builder
                .comment("Hand Biolith the registries at the start of WorldStem instead of the end. BCLib asks every vanilla biome source for its biome list from inside that constructor, which is before Biolith has a registry to look biomes up in, and Biolith throws.")
                .define("earlyRegistryCapture", true), v -> biolithEarlyRegistryCapture = v);
        gate(builder
                .comment("Return Biolith's cached biome list without entering its lock. Biolith synchronizes on the biome source on every single biome lookup, and world generation runs those lookups on several threads at once.")
                .define("reuseBiomeEntries", true), v -> biolithReuseBiomeEntries = v);
        builder.pop();

        builder.comment("TerraBlender patches.").push("terrablender");
        gate(builder
                .comment("Cache TerraBlender's per-block namespace lookup for the duration of a biome.")
                .define("cacheNamespaceRule", true), v -> terrablenderCacheNamespaceRule = v);
        builder.pop();

        builder.comment("Terramity patches.").push("terramity");
        gate(builder
                .comment("Bail out of Terramity's held item animation handler before it copies your hands.")
                .define("skipItemAnimationCopies", true), v -> terramitySkipItemAnimationCopies = v);
        gate(builder
                .comment("Skip Terramity's entity animation handler for entities that are not Terramity's.")
                .define("skipForeignEntityAnimations", true), v -> terramitySkipForeignEntityAnimations = v);
        gate(builder
                .comment("Reuse the answer when a Terramity procedure raytraces the same ray twice in a row.")
                .define("memoizeProcedureRaycasts", true), v -> terramityMemoizeProcedureRaycasts = v);
        gate(builder
                .comment("Skip the four Terramity accessory tick procedures on the client, where they do nothing.")
                .define("skipClientCurioScans", true), v -> terramitySkipClientCurioScans = v);
        gate(builder
                .comment("Bail out of Terramity's armour animation handler before it re-reads your equipment.")
                .define("skipArmorAnimationScan", true), v -> terramitySkipArmorAnimationScan = v);
        gate(builder
                .comment("Keep Terramity's screen shaders to your own player and to shaders Terramity loaded.")
                .define("fixPhasingShaderStomp", true), v -> terramityFixPhasingShaderStomp = v);
        builder.pop();

        builder.comment("Armageddon patches.").push("armageddon");
        gate(builder
                .comment("Skip Armageddon's entity animation handler for entities that are not Armageddon's.")
                .define("skipForeignEntityAnimations", true), v -> armageddonSkipForeignEntityAnimations = v);
        gate(builder
                .comment("Stop Armageddon's progression gate from re-parsing the same identifiers every tick.")
                .define("cacheProgressionIds", true), v -> armageddonCacheProgressionIds = v);
        builder.pop();

        builder.comment("Born in Chaos patches.").push("borninchaos");
        gate(builder
                .comment("Bail out of Born in Chaos' held item animation handler before it copies your hands.")
                .define("skipItemAnimationCopies", true), v -> borninchaosSkipItemAnimationCopies = v);
        gate(builder
                .comment("Skip Born in Chaos' entity animation handler for entities that are not its own.")
                .define("skipForeignEntityAnimations", true), v -> borninchaosSkipForeignEntityAnimations = v);
        gate(builder
                .comment("Stop 83 Born in Chaos mobs from resizing themselves once per tick for no reason.")
                .define("skipRedundantDimensionRefresh", true), v -> borninchaosSkipRedundantDimensionRefresh = v);
        gate(builder
                .comment("Narrow the four Born in Chaos minion claim scans to the mod's own entities.")
                .define("narrowMinionScans", true), v -> borninchaosNarrowMinionScans = v);
        builder.pop();

        builder.comment("Critters n' Crawlers patches.").push("cnc");
        gate(builder
                .comment("Skip the twenty seven 'have you met this animal yet' player tick handlers on the client. Each one scans a four block box for its own mob and walks your whole inventory before it looks at the flag it is trying to set, and the client copy of that flag is overwritten by the server sync anyway.")
                .define("skipClientEncounterScans", true), v -> cncSkipClientEncounterScans = v);
        gate(builder
                .comment("Only let those twenty seven handlers run every this many ticks on the server. Stock runs all of them every tick for every player forever, including long after every animal has been met. A toast can appear up to interval minus one ticks later than stock. Set to 1 for stock behaviour.")
                .defineInRange("encounterScanInterval", 5, 1, 40), 1, v -> cncEncounterScanInterval = v);
        gate(builder
                .comment("Skip the mod's twenty eight way instanceof chain for entities that are not its own.")
                .define("skipForeignEntityAnimations", true), v -> cncSkipForeignEntityAnimations = v);
        gate(builder
                .comment("Stop the mod's twenty eight mobs from resizing themselves once per tick for no reason. Their hitbox only ever changes with baby state, which the game already refreshes on its own.")
                .define("skipRedundantDimensionRefresh", true), v -> cncSkipRedundantDimensionRefresh = v);
        gate(builder
                .comment("Only run the caribou trample check while a caribou is actually loaded. Stock builds a box, an entity scan, a comparator and a sorted stream for every living entity in the world every tick, whether or not the mod's caribou exists.")
                .define("gateCaribouDashScan", true), v -> cncGateCaribouDashScan = v);
        gate(builder
                .comment("Look for a nearby wechuge once per client tick instead of four times per rendered frame. The fog colour and fog distance handlers each run two entity scans every frame just to ask whether one is close by.")
                .define("cacheWechugeFogScan", true), v -> cncCacheWechugeFogScan = v);
        gate(builder
                .comment("Only let the sasquatch decide whether to wear its hidden texture every this many ticks. Stock runs two sixteen block player scans per sasquatch per tick to make that call. Set to 1 for stock behaviour.")
                .defineInRange("sasquatchTextureScanInterval", 4, 1, 40), 1, v -> cncSasquatchTextureScanInterval = v);
        gate(builder
                .comment("Skip the sasquatch wood knock handler on the client, where its two hundred and sixteen block reads per sasquatch per tick can only end in a sound the client never plays.")
                .define("skipClientWoodKnockScan", true), v -> cncSkipClientWoodKnockScan = v);
        gate(builder
                .comment("Keep the Sobbing screen shader to your own player and to shaders Critters n' Crawlers loaded. Stock reacts to every player it ticks and tears down whatever post effect happens to be running, whether or not the mod put it there.")
                .define("fixSobbingShaderStomp", true), v -> cncFixSobbingShaderStomp = v);
        builder.pop();

        builder.comment("Critters and Companions patches.").push("crittersandcompanions");
        gate(builder
                .comment("Only run the red panda flee scan while a red panda is actually loaded. Critters and Companions bolts a priority minus one avoid goal onto bees, endermen, iron golems, llamas, polar bears, spiders, vexes and wolves, and the goal selector asks that goal whether it wants to start every other tick, which is a thirty three by eight by thirty three entity scan plus a fresh box and list per mob forever. Result is identical because a world with no red panda in it cannot contain the red panda the scan is looking for.")
                .define("gateRedPandaAvoidGoal", true), v -> crittersandcompanionsGateRedPandaAvoidGoal = v);
        gate(builder
                .comment("Only look for the three nearby koi fish that grant Luck every this many ticks. Stock runs a twenty block wide koi scan for every player every server tick, and the Luck it hands out already lasts two hundred and ten ticks, so the effect timer just ticks down a little between refreshes. Set to 1 for stock behaviour.")
                .defineInRange("koiLuckScanInterval", 10, 1, 40), 1, v -> crittersandcompanionsKoiLuckScanInterval = v);
        gate(builder
                .comment("Work out which pearl necklace you are wearing once per player per tick instead of once per question. The lookup builds six chained streams over your thirty six inventory slots plus every curios slot, and every drowned and guardian that considers you as a target asks it again, so a dozen of them was a dozen full walks. A necklace swapped mid tick reads stale until the next tick.")
                .define("memoNecklaceLookup", true), v -> crittersandcompanionsMemoNecklaceLookup = v);
        gate(builder
                .comment("Return from the silk leash renderer before it opens an iterator over an empty leash list. Critters and Companions calls it at the end of every living entity render and every geckolib entity render, so on a modded pack that is one throwaway iterator per visible mob per frame.")
                .define("skipEmptyLeashRender", true), v -> crittersandcompanionsSkipEmptyLeashRender = v);
        gate(builder
                .comment("Only let an empty handed otter or leaf insect look for dropped food every this many ticks. Each one runs a twenty four block wide item entity scan every tick while it has nothing in hand. A critter can notice a dropped item up to interval minus one ticks later than stock, and a critter that already found food keeps scanning every tick so its goal does not stop early. Set to 1 for stock behaviour.")
                .defineInRange("critterItemScanInterval", 4, 1, 20), 1, v -> crittersandcompanionsCritterItemScanInterval = v);
        gate(builder
                .comment("Read a critter behaviour straight out of its map instead of wrapping it in two Optionals, and skip the behaviour walk entirely when a mob has none. Critters and Companions attaches a behaviour map to every entity in the game and walks it from Mob tick, Mob aiStep and LivingEntity travel, and its models ask for a behaviour by class every frame. Result is identical.")
                .define("fastBehaviourLookup", true), v -> crittersandcompanionsFastBehaviourLookup = v);
        builder.pop();

        builder.comment("Cucumber Library patches.").push("cucumber");
        gate(builder
                .comment("Look for a player within sixty four blocks before serialising a block entity, not after. Cucumber's dispatch helper writes the whole tile, inventory NBT and all, into a fresh update packet and only then asks whether anyone is close enough to receive it, and every Mystical Agriculture machine calls it at the end of every tick it changed, which for a running machine is every tick. Also swaps the Math.hypot distance test for the squared distance. Result is identical.")
                .define("leanTileDispatch", true), v -> cucumberLeanTileDispatch = v);
        gate(builder
                .comment("Ask whether an item has any tags instead of building the full lists, when you are not holding CTRL. With advanced tooltips on, stock collects every block tag, every item tag and every fluid tag, running a fluid handler capability lookup and a sorted distinct pass over them, once per hovered item per frame, and then throws all of it away to print 'Hold CTRL for tags'. Result is identical.")
                .define("leanTagTooltip", true), v -> cucumberLeanTagTooltip = v);
        gate(builder
                .comment("Remember which item a tag resolved to instead of re-reading config/cucumber-tags.json off disk. Cucumber only caches a tag once the file has been parsed successfully, so every tag it has not seen yet costs a file open plus a full Gson parse, and a malformed cucumber-tags.json makes that miss permanent: every ShapedTagRecipe in the recipe sync packet re-reads the broken file and logs a full stack trace, on the netty thread, once per player join. The memo is dropped whenever Cucumber rebuilds its own tag map, so it lives exactly as long as stock's. Result is identical.")
                .define("cacheTagLookup", true), v -> cucumberCacheTagLookup = v);
        builder.pop();

        builder.comment("Dynamic Trees patches.").push("dynamictrees");
        gate(builder
                .comment("Read each block once while Dynamic Trees decides whether a leaf can go somewhere. Stock reads the target block three times and the block under it twice for a single yes or no, and the light check under it allocates a fresh position for the block below plus one per smother layer above. This check runs six times per leaf per ageing pass, and a tree generated by world gen ages its whole leaf cluster three times, so it is the single hottest thing the mod does while chunks are being built. The smother scan also stops at the first gap instead of always walking all four layers. Result is identical.")
                .define("leanLeafPlacement", true), v -> dynamictreesLeanLeafPlacement = v);
        gate(builder
                .comment("Reuse one position object and one cell array while Dynamic Trees works out a leaf's hydration from its six neighbours. Stock allocates a six wide cell array plus a fresh BlockPos per side every time, and this runs for every leaf on every hydration update, every leaf ageing pass and every leaf it tries to grow, on both the world gen threads and the server thread. Result is identical.")
                .define("leanLeafHydration", true), v -> dynamictreesLeanLeafHydration = v);
        gate(builder
                .comment("Hand out a prebuilt collision shape for thick trunks instead of building a new one per query. Any branch wider than a full block skips Dynamic Trees' own shape cache and builds a fresh AABB and VoxelShape every single time the game asks for its collision box, which is once per axis per entity per tick for anything walking near a big trunk. There are only sixteen possible widths so they are all built once at load. Result is identical.")
                .define("cacheThickTrunkShape", true), v -> dynamictreesCacheThickTrunkShape = v);
        builder.pop();

        builder.comment("Mystical Agriculture patches.").push("mysticalagriculture");
        gate(builder
                .comment("Read augments off a tinkerable with interned slot keys and a remembered id lookup. Stock builds the string 'Augment-' plus the slot number twice per slot, parses a fresh ResourceLocation for every augment it finds and allocates five lists for one set of armour, and it does that from inventoryTick on all twelve essence tools, from onArmorTick on all four armour pieces and from the armour augment tick handler, on both sides. Result is identical.")
                .define("leanAugmentLookup", true), v -> mysticalagricultureLeanAugmentLookup = v);
        gate(builder
                .comment("Walk the augment ability cache with a plain loop and only build the player key once something is actually cached. Stock concatenates the player name and side into a fresh string and runs a stream, a filter, a map and a collector over the whole cache for every player every tick on both sides, even when nobody in the world has an augment equipped. Result is identical.")
                .define("leanAbilityCache", true), v -> mysticalagricultureLeanAbilityCache = v);
        gate(builder
                .comment("Skip the Soul Extractor's recipe lookup while its input slot is empty. Every other machine in the mod checks its input first, this one does not, so an idle extractor runs a full linear scan of every soul extraction recipe every tick and allocates an inventory wrapper for each one. An empty input cannot match any recipe, so the result is identical.")
                .define("skipIdleSoulExtractor", true), v -> mysticalagricultureSkipIdleSoulExtractor = v);
        gate(builder
                .comment("Remember a crop's translation key instead of rebuilding it with String.format on every name lookup. The mystical seed and essence items, roughly a hundred of them, resolve their name and their description id through it, so it runs once per hovered item per frame and again whenever a recipe viewer indexes or searches names. Result is identical.")
                .define("memoCropNameKey", true), v -> mysticalagricultureMemoCropNameKey = v);
        builder.pop();

        builder.comment("Skarrier Mobs patches.").push("skarriermobs");
        gate(builder
                .comment("Skip Skarrier Mobs' 17 way instanceof chain for entities that are not its own.")
                .define("skipForeignEntityAnimations", true), v -> skarriermobsSkipForeignEntityAnimations = v);
        gate(builder
                .comment("Stop the daylight burn handler from rebuilding its entity tag key for every living entity every tick.")
                .define("leanDaylightBurnScan", true), v -> skarriermobsLeanDaylightBurnScan = v);
        gate(builder
                .comment("Stop the Resisteel tool handler from rebuilding its item tag key for every living entity every tick.")
                .define("leanResisteelToolScan", true), v -> skarriermobsLeanResisteelToolScan = v);
        gate(builder
                .comment("Stop the Resisteel sword handler from firing a MobEffectEvent.Remove at every living entity every tick.")
                .define("leanResisteelSwordScan", true), v -> skarriermobsLeanResisteelSwordScan = v);
        gate(builder
                .comment("Stop the Resisteel set handler from writing five NBT values into every living entity every tick.")
                .define("leanResisteelSetTracking", true), v -> skarriermobsLeanResisteelSetTracking = v);
        gate(builder
                .comment("Replace fourteen sorted region scans that only ask whether a mob's own target is close by with one box test.")
                .define("leanTargetProximityScans", true), v -> skarriermobsLeanTargetProximityScans = v);
        gate(builder
                .comment("Narrow the rest of the mob region scans to the players, owners, monsters or flore heads their loop bodies actually use.")
                .define("narrowRegionScans", true), v -> skarriermobsNarrowRegionScans = v);
        builder.pop();

        builder.comment("Industrial Foregoing patches.").push("industrialforegoing");
        gate(builder
                .comment("Stop the Stasis Chamber tick filter from reading, and thereby creating, a ForgeData compound on every mob in the world.")
                .define("skipStasisTagChurn", true), v -> industrialforegoingSkipStasisTagChurn = v);
        builder.pop();

        builder.comment("Enigmatic Addons patches.").push("enigmaticaddons");
        gate(builder
                .comment("Stop the addon event handler and the Annihilating Sword, Violence Scroll and Extradimensional Scepter tick handlers from reading, and thereby creating, a ForgeData compound on every entity in the world. Also removes the UUID parse and ImmutableMultimap build the sword did for every living entity every tick.")
                .define("skipUnsetPersistentData", true), v -> enigmaticaddonsSkipUnsetPersistentData = v);
        gate(builder
                .comment("Skip the Frost Protection armour scan on entities that have no freeze ticks. The addon looks the enchantment up twice for every living entity every tick, once in its own living tick handler and once through the canFreeze injector that its LivingEntity tick handler calls before checking whether the entity is freezing at all, and the second one drags two Curios inventory walks along with it. Both call sites do nothing at zero freeze ticks.")
                .define("skipIdleFrostScan", true), v -> enigmaticaddonsSkipIdleFrostScan = v);
        builder.pop();

        builder.comment("Enigmatic Delicacy patches.").push("enigmaticdelicacy");
        gate(builder
                .comment("Stop the Slicing enchantment tick handler from reading, and thereby creating, a ForgeData compound on every entity on both sides every tick.")
                .define("skipUnsetPersistentData", true), v -> enigmaticdelicacySkipUnsetPersistentData = v);
        builder.pop();

        builder.comment("Enigmatic Legacy patches.").push("enigmaticlegacy");
        gate(builder
                .comment("How often the Heart of the Guardian runs its monster scan, in ticks. The item scans every hostile in a forty eight block cube every tick for every copy in an inventory, before it checks whether the wearer is cursed or whether the active ability is off cooldown, and then line of sight traces from every guardian it finds. One is the vanilla rate.")
                .defineInRange("guardianHeartScanInterval", 4, 1, 40), 1, v -> enigmaticlegacyGuardianHeartScanInterval = v);
        gate(builder
                .comment("Ask the Cursed Ring anger scan for the piglins and neutral mobs its loop can actually anger instead of every living entity in a forty eight block cube. The loop computes an armour and invisibility visibility fraction for every entity it is handed and then discards everything that is not a piglin or a neutral mob. Result is identical.")
                .define("narrowCursedRingAngerScan", true), v -> enigmaticlegacyNarrowCursedRingAngerScan = v);
        gate(builder
                .comment("Drop removed entities from the angered guardian map when a new one is added. Enigmatic Legacy keeps every guardian that has ever targeted a Heart of the Guardian bearer in a strong keyed multimap and only clears it when a world is loaded, so a session at a guardian farm retains every dead guardian.")
                .define("pruneAngeredGuardians", true), v -> enigmaticlegacyPruneAngeredGuardians = v);
        builder.pop();

        builder.comment("Traveloptics patches.").push("traveloptics");
        gate(builder
                .comment("Check for the Spider Aspect effect before walking every living entity's curios inventory every tick.")
                .define("leanClimbCurioScan", true), v -> travelopticsLeanClimbCurioScan = v);
        gate(builder
                .comment("Check that the entity is a spell casting mob before reading its effect map in the Blackout and Casting handlers.")
                .define("leanCastEffectChecks", true), v -> travelopticsLeanCastEffectChecks = v);
        builder.pop();

        builder.comment("Celestial Enchantments patches.").push("celestialenchantments");
        gate(builder
                .comment("Skip the seven map, two array clone enchantment scan for living entities that have nothing enchanted equipped.")
                .define("skipUnenchantedTick", true), v -> celestialenchantmentsSkipUnenchantedTick = v);
        gate(builder
                .comment("Skip the per slot enchantment map allocation for equipment slots that hold nothing enchanted.")
                .define("leanSlotEnchScan", true), v -> celestialenchantmentsLeanSlotEnchScan = v);
        builder.pop();

        builder.comment("Unique Accessories patches.").push("uniqueaccessories");
        gate(builder
                .comment("Answer the Waist Warmer curios lookup from the shared per tick curio presence cache instead of walking the inventory for every living entity every tick.")
                .define("leanWaistWarmerScan", true), v -> uniqueaccessoriesLeanWaistWarmerScan = v);
        gate(builder
                .comment("Stop the Suspicious Mushroom and Rose of Temptation handlers from attaching an empty ForgeData tag to every entity in the world.")
                .define("skipUnsetPersistentData", true), v -> uniqueaccessoriesSkipUnsetPersistentData = v);
        builder.pop();

        builder.comment("Blood Magic patches.").push("bloodmagic");
        gate(builder
                .comment("Stop rebuilding the whole ARC recipe list on every tick of every ARC.")
                .define("cacheArcRecipeList", true), v -> bloodmagicCacheArcRecipeList = v);
        gate(builder
                .comment("Give the ARC's furnace mode the recipe cache vanilla furnaces already have.")
                .define("cacheArcFurnaceRecipe", true), v -> bloodmagicCacheArcFurnaceRecipe = v);
        gate(builder
                .comment("Give the item routing network's connectivity search a visited set that is actually a set.")
                .define("fastRoutingConnectivity", true), v -> bloodmagicFastRoutingConnectivity = v);
        builder.pop();

        builder.comment("Animus patches.").push("animus");
        gate(builder
                .comment("Stop rebuilding the equivalency sigil's block outline every single frame.")
                .define("cacheEquivalencyPreview", true), v -> animusCacheEquivalencyPreview = v);
        builder.pop();

        builder.comment("Patchouli patches.").push("patchouli");
        gate(builder
                .comment("Answer 'is this item one of the guide books' from a per item cache.")
                .define("cacheBookItemLookup", true), v -> patchouliCacheBookItemLookup = v);
        builder.pop();

        builder.comment("Structurify patches.").push("structurify");
        gate(builder
                .comment("Resolve Structurify's per structure set config lookup in one hash lookup.")
                .define("fastStructureSetLookup", true), v -> structurifyFastStructureSetLookup = v);
        gate(builder
                .comment("Skip Structurify's structure check bookkeeping when none of its checks are enabled.")
                .define("skipDisabledStructureChecks", true), v -> structurifySkipDisabledStructureChecks = v);
        gate(builder
                .comment("Replace Structurify's terrain height cache with a primitive keyed one.")
                .define("leanHeightCache", true), v -> structurifyLeanHeightCache = v);
        gate(builder
                .comment("Collect overlap check section keys without boxing them.")
                .define("leanOverlapSections", true), v -> structurifyLeanOverlapSections = v);
        gate(builder
                .comment("Stop rebuilding a structure set's entry list on every read.")
                .define("cacheStructureSetEntries", true), v -> structurifyCacheStructureSetEntries = v);
        gate(builder
                .comment("Restore vanilla's plain getStartForStructure while Structurify's checks are idle.")
                .define("skipStartCheckWrap", true), v -> structurifySkipStartCheckWrap = v);
        builder.pop();

        builder.comment("Bosses' Rises patches.").push("bossesrise");
        gate(builder
                .comment("Only collect Bosses' Rises entities in its per player cinematic scan.")
                .define("narrowCinematicScan", true), v -> bossesriseNarrowCinematicScan = v);
        gate(builder
                .comment("Walk only Bosses' Rises entities in the per frame boss VFX pass instead of every entity in the level.")
                .define("leanVfxScan", true), v -> bossesriseLeanVfxScan = v);
        builder.pop();

        builder.comment("Marium's Soulslike Weaponry patches.").push("soulsweapons");
        gate(builder
                .comment("Read the mod's despawn timer without writing to every entity that does not have one.")
                .define("leanDespawnTimer", true), v -> soulsweaponsLeanDespawnTimer = v);
        builder.pop();

        builder.comment("Kind of Nice Weapon patches.").push("konweapon");
        gate(builder
                .comment("Bail out of the held item animation handler before it copies your hands.")
                .define("skipItemAnimationCopies", true), v -> konweaponSkipItemAnimationCopies = v);
        builder.pop();

        builder.comment("Immersive Aircraft patches.").push("immersiveaircraft");
        gate(builder
                .comment("Batch the aircraft HUD instead of flushing the GUI buffer after every primitive.")
                .define("batchOverlay", true), v -> immersiveaircraftBatchOverlay = v);
        builder.pop();

        builder.comment("FTB Chunks patches.").push("ftbchunks");
        gate(builder
                .comment("Stop building the minimap texture when the minimap is not being shown.")
                .define("skipHiddenMinimapWork", true), v -> ftbchunksSkipHiddenMinimapWork = v);
        gate(builder
                .comment("Copy a map region's five images straight into their pixel arrays instead of making 1.3 million per pixel setRGB calls on the calling thread.")
                .define("fastRegionWrite", true), v -> ftbchunksFastRegionWrite = v);
        gate(builder
                .comment("Reuse the region and its image across the 225 chunk tiles of one minimap rebuild instead of taking the shared map lock for every tile.")
                .define("memoMinimapRegions", true), v -> ftbchunksMemoMinimapRegions = v);
        builder.pop();

        builder.comment("Regions Unexplored patches.").push("regionsunexplored");
        gate(builder
                .comment("Remember what the furnace fuel handler decided for an item instead of walking its 1100 branch block comparison chain again.")
                .define("cacheFurnaceBurnTimes", true), v -> regionsunexploredCacheFurnaceBurnTimes = v);
        builder.pop();

        builder.comment("Gnetum patches.").push("gnetum");
        gate(builder
                .comment("Resolve an event handler's mod id with one map lookup instead of a containsKey followed by a get.")
                .define("singleModIdLookup", true), v -> gnetumSingleModIdLookup = v);
        builder.pop();

        builder.comment("Ending Library patches.").push("endinglibrary");
        gate(builder
                .comment("Skip building the capability name fallback on every camera capability lookup when the registered capability is already resolved.")
                .define("leanCameraCapLookup", true), v -> endinglibraryLeanCameraCapLookup = v);
        builder.pop();

        builder.comment("Punchy patches.").push("punchy");
        gate(builder
                .comment("Remember which resource paths no pack contains, so a repeated miss stops rescanning every loaded pack.")
                .define("cacheResourceStackMisses", true), v -> punchyCacheResourceStackMisses = v);
        builder.pop();

        builder.comment("L2 Hostility patches.").push("l2hostility");
        gate(builder
                .comment("Stop re-asking entities that cannot have traits for their trait capability.")
                .define("skipTraitlessCapLookup", true), v -> l2hostilitySkipTraitlessCapLookup = v);
        builder.pop();

        builder.comment("Ice and Fire patches.").push("iceandfire");
        gate(builder
                .comment("Keep Ice and Fire's EntityData capability handle on the entity instead of in a boxed map.")
                .define("fastEntityDataLookup", true), v -> iceandfireFastEntityDataLookup = v);
        gate(builder
                .comment("Stop rebuilding the pathfinding debug render context on every render stage.")
                .define("skipPathDebugRender", true), v -> iceandfireSkipPathDebugRender = v);
        gate(builder
                .comment("Test dragon armour with the four slot ordinals instead of two built strings.")
                .define("skipEmptyArmorLayer", true), v -> iceandfireSkipEmptyArmorLayer = v);
        gate(builder
                .comment("Key the dragon layered-texture cache on a packed int instead of a built string.")
                .define("cacheDragonTexture", true), v -> iceandfireCacheDragonTexture = v);
        gate(builder
                .comment("Return early from the dragon banner and rider layers when they would draw nothing.")
                .define("skipEmptyDragonLayers", true), v -> iceandfireSkipEmptyDragonLayers = v);
        gate(builder
                .comment("Trim the per-tick work of dragon, sea serpent and death worm body parts.")
                .define("leanMultipartTick", true), v -> iceandfireLeanMultipartTick = v);
        gate(builder
                .comment("Vertical half-extent, in blocks, of the dragon target search box. -1 keeps stock.")
                .defineInRange("dragonTargetSearchHeight", 32, -1, 2048), -1, v -> iceandfireDragonTargetSearchHeight = v);
        builder.pop();

        builder.comment("Ice and Fire dragon dens as structures. Ported from IAF Dragon Fix (MIT).").push("iafdragonfix");
        gate(builder
                .comment("Generate dragon roosts and caves as structures instead of as decoration features.")
                .define("structureDens", true), v -> iafdragonfixStructureDens = v);
        plain(builder
                .comment("Minimum distance in blocks from world spawn before a dragon roost will generate.")
                .defineInRange("roostSpawnDistance", 800, 0, 100000), v -> iafdragonfixRoostSpawnDistance = v);
        plain(builder
                .comment("Minimum distance in blocks from world spawn before a dragon cave will generate.")
                .defineInRange("caveSpawnDistance", 800, 0, 100000), v -> iafdragonfixCaveSpawnDistance = v);
        builder.pop();

        builder.comment("Mowzie's Mobs patches.").push("mowziesmobs");
        gate(builder
                .comment("Keep Mowzie's Mobs' four capability handles on the entity instead of re-resolving them.")
                .define("fastCapabilityLookup", true), v -> mowziesmobsFastCapabilityLookup = v);
        gate(builder
                .comment("Stop Mowzie's Mobs attaching its capabilities to every entity twice.")
                .define("dedupeCapabilityAttach", true), v -> mowziesmobsDedupeCapabilityAttach = v);
        gate(builder
                .comment("Reuse the camera shake entity scan within a client tick instead of once per frame.")
                .define("cacheCameraShakeScan", true), v -> mowziesmobsCacheCameraShakeScan = v);
        gate(builder
                .comment("Ticks between boss music state packets. 1 keeps stock behaviour.")
                .defineInRange("bossMusicPacketInterval", 5, 1, 100), 1, v -> mowziesmobsBossMusicPacketInterval = v);
        gate(builder
                .comment("Drop the throwaway Optional from Mowzie's per-frame bone lookups.")
                .define("leanBoneLookup", true), v -> mowziesmobsLeanBoneLookup = v);
        gate(builder
                .comment("Compute the dynamic chain's render matrix once per chain instead of once per bone.")
                .define("hoistChainRenderMatrix", true), v -> mowziesmobsHoistChainRenderMatrix = v);
        gate(builder
                .comment("Maximum physics substeps per frame for dynamic chains. 0 disables the clamp.")
                .defineInRange("dynamicChainSubstepCap", 4, 0, 64), 0, v -> mowziesmobsDynamicChainSubstepCap = v);
        gate(builder
                .comment("Stop every Umvuthana follower re-scanning a 64x64x64 region for its leader every tick.")
                .define("cacheUmvuthanaLeader", true), v -> mowziesmobsCacheUmvuthanaLeader = v);
        gate(builder
                .comment("Reuse one vector pair per cube in Mowzie's llibrary model renderer instead of allocating 30 per cube per frame.")
                .define("leanModelBoxVectors", true), v -> mowziesmobsLeanModelBoxVectors = v);
        gate(builder
                .comment("Skip the Elokosa transformation layer's full model re-render while it is fully transparent.")
                .define("skipBlankElokosaTransform", true), v -> mowziesmobsSkipBlankElokosaTransform = v);
        gate(builder
                .comment("Test the bone name before pushing a matrix in the Umvuthana and Umvuthi render layers.")
                .define("leanLayerBoneScan", true), v -> mowziesmobsLeanLayerBoneScan = v);
        gate(builder
                .comment("Build the sunstrike, solar beam and solar flare render types once instead of once per entity per frame.")
                .define("cacheEffectRenderTypes", true), v -> mowziesmobsCacheEffectRenderTypes = v);
        builder.pop();

        builder.comment("Pick Up Notifier patches.").push("pickupnotifier");
        gate(builder
                .comment("Draw fully opaque pick-up sprites straight to the screen instead of routing every one through a window sized off-screen render target and a full screen blit.")
                .define("skipOpaqueSpriteBuffer", true), v -> pickupnotifierSkipOpaqueSpriteBuffer = v);
        builder.pop();

        builder.comment("Placebo patches.").push("placebo");
        gate(builder
                .comment("Skip the HashMap, event object and event bus dispatch Placebo builds on every single ItemStack enchantment level lookup while nothing is listening to GetEnchantmentLevelEvent.")
                .define("skipEmptyEnchantmentEvent", true), v -> placeboSkipEmptyEnchantmentEvent = v);
        builder.pop();

        builder.comment("Photon patches.").push("photon");
        gate(builder
                .comment("Emit billboard particle vertices from reusable scratch vectors instead of allocating around ten JOML objects per particle per frame.")
                .define("leanParticleQuads", true), v -> photonLeanParticleQuads = v);
        gate(builder
                .comment("Reuse the particle's block position for the per tick light lookup while it stays inside the same block.")
                .define("leanParticleLight", true), v -> photonLeanParticleLight = v);
        gate(builder
                .comment("Build trail ribbons from float locals instead of allocating about ten vectors per trail segment per frame.")
                .define("leanTrailVertices", true), v -> photonLeanTrailVertices = v);
        gate(builder
                .comment("Drop the emptied block effect cache entry instead of leaving one map entry and one empty list per block position for the rest of the session.")
                .define("dropEmptyEffectCacheEntries", true), v -> photonDropEmptyEffectCacheEntries = v);
        builder.pop();

        builder.comment("Integrated API patches.").push("integratedapi");
        gate(builder
                .comment("Return early from the enhanced terrain adaptation pass when there is none in range.")
                .define("skipEmptyBeardifier", true), v -> integratedapiSkipEmptyBeardifier = v);
        builder.pop();

        builder.comment("Echelon patches.").push("echelon");
        gate(builder
                .comment("Stop recomputing an MD5 for every tier attribute modifier.")
                .define("cacheTierAttributeUuids", true), v -> echelonCacheTierAttributeUuids = v);
        builder.pop();

        builder.comment("Elysium API patches.").push("elysiumapi");
        gate(builder
                .comment("Answer a repeated climate sample for the same position from a one entry cache.")
                .define("memoClimateSample", true), v -> elysiumapiMemoClimateSample = v);
        gate(builder
                .comment("Skip Elysium API's duplicate biome resolution entirely when no biome replacer exists.")
                .define("skipUnusedBiomeReplacerLookup", true), v -> elysiumapiSkipUnusedBiomeReplacerLookup = v);
        builder.pop();

        builder.comment("Enigmatic Dice patches.").push("enigmaticdice");
        gate(builder
                .comment("Answer the isWearing checks for the Moai Charm, Ring of Agility and Divine Shield from the per-tick curio set.")
                .define("fastCurioMiss", true), v -> enigmaticdiceFastCurioMiss = v);
        builder.pop();

        builder.comment("Balm patches.").push("balm");
        gate(builder
                .comment("Stop rebuilding a block state's toString on every quad request.")
                .define("memoDynamicModelKeys", true), v -> balmMemoDynamicModelKeys = v);
        builder.pop();

        builder.comment("Moonlight Lib patches.").push("moonlight");
        gate(builder
                .comment("Skip Moonlight's custom map marker refresh on maps that have no markers.")
                .define("skipEmptyMapMarkerScan", true), v -> moonlightSkipEmptyMapMarkerScan = v);
        builder.pop();

        builder.comment("Pehkui patches.").push("pehkui");
        gate(builder
                .comment("Stop allocating two throwaway lambdas per scale type per entity per tick.")
                .define("leanScaleTick", true), v -> pehkuiLeanScaleTick = v);
        gate(builder
                .comment("Remember which scale type a typed scale modifier points at instead of calling its supplier again on every single scale read. WARNING: a mod that swaps the type a modifier resolves to at runtime will be pinned to the first answer.")
                .define("memoModifierType", true), v -> pehkuiMemoModifierType = v);
        gate(builder
                .comment("Work out an entity's interaction box scales once a tick instead of twice for every entity walked by every AABB query. A scale set part way through a tick is seen on the next one.")
                .define("memoInteractionBoxScales", true), v -> pehkuiMemoInteractionBoxScales = v);
        gate(builder
                .comment("Let Pehkui keep its already existing per tick scale cache on the client too. Pehkui only fills that cache on the server, so every client side scale read walks the whole modifier chain again. WARNING: if a mod changes an entity's scale without going through Pehkui's own setters, the visual size can lag by one tick. Turn this off if you see entities stuck at the wrong size.")
                .define("cacheClientScales", true), v -> pehkuiCacheClientScales = v);
        builder.pop();

        builder.comment("Relics patches.").push("relics");
        gate(builder
                .comment("Cap the homing speed of the Holy Locket death and life essences. Their arc step scales with both the distance to the target and their own age, so an essence that misses for long enough accelerates without bound until its query box overflows the entity section index and crashes the game.")
                .define("clampEssenceSpeed", true), v -> relicsClampEssenceSpeed = v);
        plain(builder
                .comment("Upper bound in blocks per tick for that cap. The step is also never allowed to exceed the remaining distance to the target, so the essence stops overshooting and converges instead.")
                .defineInRange("essenceMaxSpeed", 4.0D, 0.5D, 64.0D), v -> relicsEssenceMaxSpeed = v);
        builder.pop();

        builder.comment("More Relics patches.").push("morerelics");
        gate(builder
                .comment("Build the combined curio inventory once per equipped relic scan instead of twice for every single slot. More Relics calls getEquippedCurios() inside both the loop condition and the loop body, so a thirty slot player allocates sixty throwaway inventory wrappers per relic overlay per frame.")
                .define("hoistEquippedCurios", true), v -> morerelicsHoistEquippedCurios = v);
        builder.pop();

        builder.comment("Terra Curio patches.").push("terracurio");
        gate(builder
                .comment("Answer Terra Curio's 'is this accessory equipped' questions from the shared per-tick curio presence set instead of walking every curio slot handler again. CuriosUtils#noSameCurio runs once per living entity per tick out of LivingEntity#getFrictionInfluencedSpeed, and about ten more times per damage event, and almost every one of those is a miss.")
                .define("cachedCurioLookup", true), v -> terracurioCachedCurioLookup = v);
        gate(builder
                .comment("Read Terra Curio's custom attribute remap table from a plain map instead of the synchronised Hashtable it lives in. The table is filled once during setup and never written again, but ModAttributes#hasCustomAttribute is called for every living entity every tick on both the client and the server thread, so every one of those calls takes a monitor on the same shared object.")
                .define("leanAttributeMap", true), v -> terracurioLeanAttributeMap = v);
        gate(builder
                .comment("Skip Terra Curio's aggro retarget pass while no online player carries a non zero aggro value. The handler runs on every LivingChangeTargetEvent, and each run streams every player in the dimension with a distance and canAttack check, even though the result can only differ from vanilla when some player has actually modified aggro. The player scan is cached once per dimension per tick.")
                .define("skipIdleAggroScan", true), v -> terracurioSkipIdleAggroScan = v);
        gate(builder
                .comment("Only write Terra Curio's onPosIsIce flag when it actually changes. Ice Skates, Frostspark Boots and Terraspark Boots rewrite the flag onto the stack every single tick, which marks the stack dirty, so Curios resyncs the accessory to the client and reapplies its attribute modifiers every tick for as long as the boots are worn.")
                .define("skipUnchangedIceFlag", true), v -> terracurioSkipUnchangedIceFlag = v);
        builder.pop();

        builder.comment("Cosmetic Armor Reworked patches.").push("cosmeticarmor");
        gate(builder
                .comment("Keep each player's armour restore queue on the player instead of in a weak keyed Guava cache. The cache is looked up twice per rendered player per frame plus twice more for the held item and the arm, and every lookup pays a hash and a segment read.")
                .define("perPlayerRestoreQueue", true), v -> cosmeticarmorPerPlayerRestoreQueue = v);
        builder.pop();

        builder.comment("More Hitboxes patches.").push("morehitboxes");
        gate(builder
                .comment("Skip More Hitboxes' multipart pass over the result of every entity box query when the result holds no multipart. The pass allocates a hash set and re-tests the predicate for each hit, and almost every query returns none.")
                .define("skipAbsentMultiPartFilter", true), v -> morehitboxesSkipAbsentMultiPartFilter = v);
        builder.pop();

        builder.comment("Tons Of Enchants patches.").push("tonsofenchants");
        gate(builder
                .comment("Stop every player broadcasting a pointless attribute sync packet every tick.")
                .define("skipAbsentAttributeRemoval", true), v -> tonsofenchantsSkipAbsentAttributeRemoval = v);
        gate(builder
                .comment("Skip the Frostbite entity scan on the client, where it cannot do anything.")
                .define("frostbiteSkipClient", true), v -> tonsofenchantsFrostbiteSkipClient = v);
        gate(builder
                .comment("Look the attribute up once instead of three times.")
                .define("leanAttributeLookup", true), v -> tonsofenchantsLeanAttributeLookup = v);
        gate(builder
                .comment("Run each of the seventeen PlayerTickEvent listeners once a tick instead of twice.")
                .define("singlePhasePlayerTick", true), v -> tonsofenchantsSinglePhasePlayerTick = v);
        builder.pop();

        builder.comment("Subtle Effects patches.").push("subtleeffects");
        gate(builder
                .comment("Test the firefly light condition before the biome lookup instead of after it.")
                .define("fireflyDarknessGate", true), v -> subtleeffectsFireflyDarknessGate = v);
        gate(builder
                .comment("Stop scanning biome particle positions that no setting can spawn at.")
                .define("capBiomeParticleScan", true), v -> subtleeffectsCapBiomeParticleScan = v);
        gate(builder
                .comment("Drain the ticker removal queue in one pass instead of one ArrayList#remove each.")
                .define("leanTickerRemoval", true), v -> subtleeffectsLeanTickerRemoval = v);
        gate(builder
                .comment("Skip the geyser scan on blocks no geyser type can spawn on.")
                .define("geyserBlockPreFilter", true), v -> subtleeffectsGeyserBlockPreFilter = v);
        builder.pop();

        builder.comment("Ars Energistique patches.").push("arseng");
        gate(builder
                .comment("Stop registering a capability listener that can never be observed.")
                .define("skipDeadRelayListeners", true), v -> arsengSkipDeadRelayListeners = v);
        gate(builder
                .comment("Stop attaching the SOURCE_TILE wrapper to block entities that cannot back it.")
                .define("gateGenericInvWrapper", true), v -> arsengGateGenericInvWrapper = v);
        builder.pop();

        builder.comment("Perception patches.").push("perception");
        gate(builder
                .comment("Stop allocating a throwaway trail config for every entity and every particle.")
                .define("shareDefaultTrailData", true), v -> perceptionShareDefaultTrailData = v);
        builder.pop();

        builder.comment("Quark patches.").push("quark");
        gate(builder
                .comment("Stop attaching a persistent data compound to every animal in the world.")
                .define("skipPigLitterTagChurn", true), v -> quarkSkipPigLitterTagChurn = v);
        builder.pop();

        builder.comment("Zeta patches. Zeta is Quark's module and event framework.").push("zeta");
        gate(builder
                .comment("Stop allocating an iterator for every block every structure places.")
                .define("leanStructureReplacement", true), v -> zetaLeanStructureReplacement = v);
        gate(builder
                .comment("Build one Zeta event wrapper per dispatch instead of one per listener.")
                .define("shareEventWrappers", true), v -> zetaShareEventWrappers = v);
        builder.pop();

        builder.comment("Dungeon Crawl patches.").push("dungeoncrawl");
        gate(builder
                .comment("Stop asking for a block entity after placing a block that cannot have one.")
                .define("skipBlockEntityProbe", true), v -> dungeoncrawlSkipBlockEntityProbe = v);
        builder.pop();

        builder.comment("Goety patches.").push("goety");
        gate(builder
                .comment("Reuse the LazyOptional Goety's capability providers build.")
                .define("cacheCapabilityOptional", true), v -> goetyCacheCapabilityOptional = v);
        gate(builder
                .comment("Stop building the throwaway capability fallback on every query.")
                .define("skipCapabilityFallback", true), v -> goetySkipCapabilityFallback = v);
        gate(builder
                .comment("Memoise the constant attribute modifiers Goety rebuilds every tick.")
                .define("memoAttributeModifiers", true), v -> goetyMemoAttributeModifiers = v);
        gate(builder
                .comment("Answer SEHelper#isAlly without resolving anything when the player has no allies.")
                .define("fastEmptyAllyCheck", true), v -> goetyFastEmptyAllyCheck = v);
        gate(builder
                .comment("Answer CuriosFinder#findCurio(LivingEntity, Item) misses from the per-tick curio set.")
                .define("fastCurioItemMiss", true), v -> goetyFastCurioItemMiss = v);
        gate(builder
                .comment("Skip the boss music target lookup for entities that have no boss music. Client.")
                .define("skipBossMusicTargetLookup", true), v -> goetySkipBossMusicTargetLookup = v);
        gate(builder
                .comment("Run the fog listener's Wight#findWight scan once per tick instead of once per posted RenderFog event. Client.")
                .define("cacheFogWightScan", true), v -> goetyCacheFogWightScan = v);
        gate(builder
                .comment("Answer repeated CuriosFinder#findCurio(LivingEntity, Predicate) lookups from a per-tick per-entity memo.")
                .define("memoCurioFilter", true), v -> goetyMemoCurioFilter = v);
        gate(builder
                .comment("Run the camera shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> goetyCacheShakeScan = v);
        builder.pop();

        builder.comment("L_Ender's Cataclysm patches.").push("cataclysm");
        gate(builder
                .comment("Run the camera shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> cataclysmCacheShakeScan = v);
        builder.pop();

        builder.comment("Dodo's Mobs patches.").push("dodosmobs");
        gate(builder
                .comment("Run the camera shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> dodosmobsCacheShakeScan = v);
        builder.pop();

        builder.comment("EEEAB's Mobs patches.").push("eeeabsmobs");
        gate(builder
                .comment("Run the camera shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> eeeabsmobsCacheShakeScan = v);
        builder.pop();

        builder.comment("From The Shadows patches.").push("fromtheshadows");
        gate(builder
                .comment("Run the camera shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> fromtheshadowsCacheShakeScan = v);
        builder.pop();

        builder.comment("GTBCS Spell Lib patches.").push("gtbcs");
        gate(builder
                .comment("Run both camera shake entity scans once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> gtbcsCacheShakeScan = v);
        builder.pop();

        builder.comment("Legendary Monsters patches.").push("legendarymonsters");
        gate(builder
                .comment("Run the camera shake and dynamic zoom entity scans once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> legendarymonstersCacheShakeScan = v);
        builder.pop();

        builder.comment("Myths and Legends patches.").push("mythsandlegends");
        gate(builder
                .comment("Run the boss scan behind the fog and fog colour listeners once per tick instead of once per posted event. Client.")
                .define("cacheFogBossScan", true), v -> mythsandlegendsCacheFogBossScan = v);
        gate(builder
                .comment("Run the screen shake entity scan once per tick instead of once per frame. Client.")
                .define("cacheShakeScan", true), v -> mythsandlegendsCacheShakeScan = v);
        builder.pop();

        builder.comment("AmbientSounds patches.").push("ambientsounds");
        gate(builder
                .comment("Work out whether a biome matches a region's biome patterns once instead of running the regex every client tick. Client.")
                .define("memoBiomeMatch", true), v -> ambientsoundsMemoBiomeMatch = v);
        builder.pop();

        builder.comment("Ars Nouveau patches.").push("arsnouveau");
        gate(builder
                .comment("Ticks between refreshes of the offscreen sky texture, which costs a second full sky, cloud and weather pass plus a fog event post every frame. 1 refreshes once a tick, 0 restores the stock every frame behaviour. Client.")
                .defineInRange("skyTextureInterval", 1, 0, 200), 0, v -> arsnouveauSkyTextureInterval = v);
        builder.pop();

        builder.comment("Goety Revelation patches.").push("goetyrevelation");
        gate(builder
                .comment("Answer ATAHelper#hasHalo and #hasBrokenHalo from the per-tick curio set.")
                .define("cacheHaloLookup", true), v -> goetyrevelationCacheHaloLookup = v);
        builder.pop();

        builder.comment("RevelationFix patches. RevelationFix ships jar-in-jar inside Goety Revelation.")
                .push("revelationfix");
        gate(builder
                .comment("Skip the walk-on-fluid probe for entities that can never walk on fluid.")
                .define("skipMobFluidStandScan", true), v -> revelationfixSkipMobFluidStandScan = v);
        gate(builder
                .comment("Stop posting RevelationFix's hurt-by-target events for mobs that ignore them.")
                .define("skipNonSpiderHurtByTargetEvents", true), v -> revelationfixSkipNonSpiderHurtByTargetEvents = v);
        builder.pop();

        builder.comment("Macabre patches.").push("macabre");
        gate(builder
                .comment("Skip Macabre's entity animation handler for entities that are not Macabre's.")
                .define("skipForeignEntityAnimations", true), v -> macabreSkipForeignEntityAnimations = v);
        gate(builder
                .comment("Bail out of Macabre's held item animation handler before it copies your hands.")
                .define("skipItemAnimationCopies", true), v -> macabreSkipItemAnimationCopies = v);
        gate(builder
                .comment("Send Macabre's variable sync packets once per tick instead of once per assignment.")
                .define("coalesceVariableSync", true), v -> macabreCoalesceVariableSync = v);
        builder.pop();

        builder.comment("Alex's Mobs patches.").push("alexsmobs");
        gate(builder
                .comment("Stop attaching the snow leopard and tiger avoidance goals to every creeper.")
                .define("skipCreeperAvoidGoals", true), v -> alexsmobsSkipCreeperAvoidGoals = v);
        gate(builder
                .comment("How often a vanilla spider may scan for Alex's Mobs flies, in goal evaluations.")
                .defineInRange("spiderFlyScanInterval", 10, 1, 200), 1, v -> alexsmobsSpiderFlyScanInterval = v);
        gate(builder
                .comment("Release the level keyed maps Alex's Mobs never clears, when a server stops.")
                .define("releaseLevelMaps", true), v -> alexsmobsReleaseLevelMaps = v);
        builder.pop();

        builder.comment("Alex's Caves patches.").push("alexscaves");
        gate(builder
                .comment("Stop recomputing Alex's Caves' cave biome placement 96 times per column.")
                .define("memoRareBiomeQuads", true), v -> alexscavesMemoRareBiomeQuads = v);
        gate(builder
                .comment("Answer Alex's Caves' duplicate climate sample from a one entry cache.")
                .define("memoClimateSample", true), v -> alexscavesMemoClimateSample = v);
        gate(builder
                .comment("Look for screen shaking mobs once per tick instead of once per frame.")
                .define("cacheShakeScan", true), v -> alexscavesCacheShakeScan = v);
        builder.pop();

        builder.comment("Ad Astra patches.").push("adastra");
        gate(builder
                .comment("Memoise Ad Astra's per-dimension gravity and temperature constants.")
                .define("memoPlanetDefaults", true), v -> adastraMemoPlanetDefaults = v);
        builder.pop();

        builder.comment("Supplementaries patches.").push("supplementaries");
        gate(builder
                .comment("Skip the enderman skull's 64 block look ray when no player is aiming anywhere near it.")
                .define("leanEndermanSkullWatch", true), v -> supplementariesLeanEndermanSkullWatch = v);
        gate(builder
                .comment("Only run Supplementaries' antique ink chunk sync for signs instead of every block entity in every chunk sent.")
                .define("skipNonSignCapSync", true), v -> supplementariesSkipNonSignCapSync = v);
        gate(builder
                .comment("Cache the tinted map block lookup instead of walking five tags per map pixel per tick.")
                .define("memoMapTintLookup", true), v -> supplementariesMemoMapTintLookup = v);
        builder.pop();

        builder.comment("Amendments patches.").push("amendments");
        gate(builder
                .comment("Stop wall lanterns broadcasting a sway packet every tick for entities that are not moving.")
                .define("skipIdleSwaySync", true), v -> amendmentsSkipIdleSwaySync = v);
        builder.pop();

        builder.comment("Create: Copycats+ patches.").push("copycats");
        gate(builder
                .comment("Remember each block state's occlusion answer instead of re-running Copycats+' holder and instanceof checks on every canOcclude call.")
                .define("memoStateOcclusion", true), v -> copycatsMemoStateOcclusion = v);
        gate(builder
                .comment("Skip the config lookup, registry lookup and string building Copycats+ runs for every block entity and structure block that is not a copycat.")
                .define("fastMigrationChecks", true), v -> copycatsFastMigrationChecks = v);
        gate(builder
                .comment("Read the two Copycats+ client model settings at most once per second instead of twice per copycat model query.")
                .define("cachedModelConfig", true), v -> copycatsCachedModelConfig = v);
        gate(builder
                .comment("Replace the lambda and platform lookups Copycats+ runs on every Create block entity update with one cached class check.")
                .define("leanVirtualWorldCheck", true), v -> copycatsLeanVirtualWorldCheck = v);
        builder.pop();

        builder.comment("Vanilla patches. These are the only patches here that are not aimed at a specific mod.").push("vanilla");
        gate(builder
                .comment("Maximum times a dropped item stack's model is drawn.")
                .defineInRange("itemEntityRenderCap", 1, 0, 5), 0, v -> itemEntityRenderCap = v);
        gate(builder
                .comment("Resolve a rendered string's font set once instead of once per glyph.")
                .define("memoGlyphFontSet", true), v -> vanillaMemoGlyphFontSet = v);
        gate(builder
                .comment("Makes /locate a lot faster, especially for rare structures.")
                .define("fasterStructureLocation", true), v -> vanillaFasterStructureLocation = v);
        gate(builder
                .comment("Stops boats breaking into planks when you ride them off a drop.")
                .define("fixBoatFallDamage", false), v -> vanillaFixBoatFallDamage = v);
        gate(builder
                .comment("Drops from broken blocks land dead centre instead of scattering.")
                .define("predictableItemDrops", false), v -> vanillaPredictableItemDrops = v);
        gate(builder
                .comment("Stop the entity tracker allocating a SectionPos for every tracked entity every tick.")
                .define("leanTrackerSectionPos", true), v -> vanillaLeanTrackerSectionPos = v);
        gate(builder
                .comment("Do the suffocation check with a loop instead of a Java stream.")
                .define("leanSuffocationScan", true), v -> vanillaLeanSuffocationScan = v);
        gate(builder
                .comment("Stop the container sync allocating a memoizing supplier for every slot every tick.")
                .define("leanMenuBroadcast", true), v -> vanillaLeanMenuBroadcast = v);
        gate(builder
                .comment("Stop the entity tracker allocating a movement vector for entities that did not move.")
                .define("leanTrackerDelta", true), v -> vanillaLeanTrackerDelta = v);
        gate(builder
                .comment("Answer repeated biome lookups on the render thread from a small per tick cache. Fog and sky colour sample 27 biomes per call, several times a frame.")
                .define("cacheBiomeQuartLookups", true), v -> vanillaCacheBiomeQuartLookups = v);
        gate(builder
                .comment("Work out what fluid the camera is in once per camera position instead of once per caller.")
                .define("memoCameraFluid", true), v -> vanillaMemoCameraFluid = v);
        gate(builder
                .comment("Work out the sky colour once per camera position per frame. Fog setup, the sky renderer and shader uniform packs each ask for it separately and every call samples 27 biomes.")
                .define("memoSkyColour", true), v -> vanillaMemoSkyColour = v);
        fastBiomeBlendValue = gate(builder
                .comment("Blend biome colours from a cached, incrementally summed grid instead of resampling the biome under every block in the blend square. Vanilla resamples the full square for every block, which is up to 225 biome lookups per block at the default blend radius. Output is identical.")
                .define("fastBiomeBlend", true), v -> vanillaFastBiomeBlend = v);
        gate(builder
                .comment("Multiplier for the four thresholds the server uses to reject a movement packet: the 100 and 300 blocks per tick squared speed caps in handleMovePlayer, the 100 in handleMoveVehicle, and the 0.0625 desync tolerance in both. At the default of 100 a player may cover 100 blocks in a tick and land 2.5 blocks away from where the server simulated them before anything is rejected, which is what stops elytra, mounts, teleports and scaled entities getting rubberbanded on a loaded server. The block clipping checks are untouched, so a client still cannot walk into a wall. Set to 1 for vanilla behaviour.")
                .defineInRange("movementCheckSlack", 100.0D, 1.0D, 1.0E9D), 1.0D, v -> vanillaMovementCheckSlack = v);
        gate(builder
                .comment("Stop the server kicking a player with \"Flying is not enabled on this server\" after 80 ticks of unsupported hovering. The check only ever arms when allow-flight is false in server.properties, which cannot be changed without a restart, and it fires on anything that keeps you off the ground without the fly ability: elytra stalls, jetpacks, grappling hooks, scaled entities, riding a laggy vehicle. Nothing else in the game reads that setting, so turning this on leaves no other behaviour changed, and it skips a block scan on every accepted movement packet.")
                .define("disableFlyingKick", true), v -> vanillaDisableFlyingKick = v);
        gate(builder
                .comment("Every five seconds, drop dead player copies that another mod left registered as chunk loaders. Such ghosts keep hundreds of chunks loaded and spawning mobs at wherever they died until restart.")
                .define("purgeGhostPlayers", true), v -> vanillaPurgeGhostPlayers = v);
        gate(builder
                .comment("Remember which bone of a model a given name belongs to instead of walking the whole model tree again for every bone of every keyframe animation. Vanilla answers that question with a recursive stream over every part of the model, once per animated bone per entity per frame, and every walk allocates a few objects per part it visits. Result is identical.")
                .define("cacheModelPartLookups", true), v -> vanillaCacheModelPartLookups = v);
        gate(builder
                .comment("Run keyframe animations with plain loops. Vanilla allocates two capturing lambdas per animated bone and one more per animation channel, every entity every frame, which is a large part of the garbage the render thread produces. Output is identical, but a mod injecting into the middle of KeyframeAnimations.animate will not run while this is on.")
                .define("leanKeyframeAnimation", true), v -> vanillaLeanKeyframeAnimation = v);
        builder.pop();

        builder.push("gnetum");
        gate(builder
                .comment("Answer gnetum's per element caching question once per tick instead of once per element per frame. Each ask is a set lookup plus a guava cache lookup and there are dozens of elements.")
                .define("memoCacheSettings", true), v -> gnetumMemoCacheSettings = v);
        builder.pop();

        builder.comment("Create: Solar patches.").push("createsolar");
        gate(builder
                .comment("Look up Create's goggles check once instead of once per frame. Create: Solar reflects for a method that this Create version does not have, so every single frame it builds and throws a NoSuchMethodException, fills in its stack trace and gives up. The overlay it was going to draw is dead either way, this just stops it costing anything.")
                .define("memoGogglesLookup", true), v -> createsolarMemoGogglesLookup = v);
        builder.pop();

        builder.comment("MCreator mod patches.").push("mcreator");
        gate(builder
                .comment("Hand MCreator mods one shared empty player variables object instead of allocating a throwaway on every single variable read. Procedures that read variables per tick or per frame can otherwise churn hundreds of megabytes of garbage. This rewrites the mods' classes as they load, so a change here only takes effect from the next launch.")
                .define("shareDefaultPlayerVariables", true), v -> mcreatorShareDefaultPlayerVariables = v);
        builder.pop();

        builder.comment("Distant Horizons patches.").push("distanthorizons");
        gate(builder
                .comment("Drop Distant Horizons' biome wrapper caches when it unloads its world. Those caches are static, are never cleared, and are keyed on the biome registry objects the server hands out on join, so every world you leave keeps its whole biome registry alive for the rest of the session. It also means the LOD tint colours after a world change are still resolved against the previous world's biomes.")
                .define("clearBiomeCachesOnUnload", true), v -> distanthorizonsClearBiomeCachesOnUnload = v);
        gate(builder
                .comment("Remember the last biome colour while Distant Horizons blends the biome tint of an LOD block. At the default blend radius of three the blend samples forty nine neighbours per tinted block and looks each one's colour up through three concurrent hash maps, and neighbouring blocks are almost always in the same biome. Result is identical.")
                .define("memoBiomeBlendColors", true), v -> distanthorizonsMemoBiomeBlendColors = v);
        gate(builder
                .comment("Remember the last biome while Distant Horizons converts a chunk column into an LOD. Biomes are stored per four blocks but the converter asks for one per block going down the whole column, so three out of four lookups repeat the previous answer. Result is identical.")
                .define("cacheChunkBiomeLookup", true), v -> distanthorizonsCacheChunkBiomeLookup = v);
        builder.pop();

        builder.comment("Summonity patches.").push("summonity");
        gate(builder
                .comment("Remember each player's owned minion list for the rest of the tick. MinionHelper#getOwnedMinions is a 256 block wide entity scan plus a sort, and almost every minion asks for it every tick to work out its formation slot, on top of the per player tick handlers, so ten minions were ten to fifteen of those scans a tick. The list is dropped the moment any minion joins or leaves the level, so summoning, dismissing and slot counting inside the same tick still see the real state.")
                .define("cacheOwnedMinionScans", true), v -> summonityCacheOwnedMinionScans = v);
        gate(builder
                .comment("Only let an idle minion scan for a new enemy every this many ticks, staggered per minion. Every minion without a target scans a minion range wide box of mobs every tick and streams for the closest one. A minion can react up to interval minus one ticks later than stock. Set to 1 for stock behaviour.")
                .defineInRange("idleTargetScanInterval", 4, 1, 20), 1, v -> summonityIdleTargetScanInterval = v);
        gate(builder
                .comment("Keep the Stardust Dragon's list of body parts for five ticks instead of running a 128 block wide entity scan once or twice per tick per dragon head. The list is refreshed whenever a part spawns or goes away, and rescanned on the same five tick cadence the dragon already uses to repair its body chain.")
                .define("cacheDragonParts", true), v -> summonityCacheDragonParts = v);
        gate(builder
                .comment("Answer the Soloist's Seal, Polychromic Necklace and Conductive Battery 'is it equipped' checks from the shared per tick curio presence set when the answer is no. The seal and necklace checks run for every player every tick and the battery check on every minion hit, and each one walked every curio slot and built a list. A curio swapped without firing CurioChangeEvent reads stale for the rest of that tick.")
                .define("fastCurioMiss", true), v -> summonityFastCurioMiss = v);
        gate(builder
                .comment("Only send the summon slot sync packet when the used slot count actually changed. Stock sends it to every player four times a second forever.")
                .define("skipUnchangedSlotSync", true), v -> summonitySkipUnchangedSlotSync = v);
        gate(builder
                .comment("Remember the Copper Drone's two attribute modifier ids instead of rebuilding them from an MD5 of the entity uuid twice per drone per tick.")
                .define("memoDroneModifierIds", true), v -> summonityMemoDroneModifierIds = v);
        builder.pop();
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        bake();
    }

    public static void onReload(ModConfigEvent.Reloading event) {
        bake();
    }

    public static void setFastBiomeBlend(boolean value) {
        vanillaFastBiomeBlend = masterEnabled && value;
        if (SPEC.isLoaded()) {
            fastBiomeBlendValue.set(value);
        }
    }

    public static void save() {
        if (SPEC.isLoaded()) {
            SPEC.save();
        }
    }

    private static void bake() {
        if (!SPEC.isLoaded()) {
            return;
        }
        for (Runnable baker : BAKERS) {
            baker.run();
        }
    }
}
