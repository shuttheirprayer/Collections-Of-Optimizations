package com.misanthropy.collections_of_optimizations.core;

import com.google.common.base.Predicates;
import einstein.subtle_effects.compat.CompatHelper;
import einstein.subtle_effects.compat.SereneSeasonsCompat;
import einstein.subtle_effects.configs.environment.BiomeConfigs;
import einstein.subtle_effects.configs.environment.FireflyConfigs;
import einstein.subtle_effects.data.SparkProviderReloadListener;
import einstein.subtle_effects.init.ModBlockTickers;
import einstein.subtle_effects.init.ModConfigs;
import einstein.subtle_effects.init.ModSounds;
import einstein.subtle_effects.ticking.BiomeEffectsManager;
import einstein.subtle_effects.ticking.SparkProviderManager;
import einstein.subtle_effects.ticking.tickers.ChestBlockEntityTicker;
import einstein.subtle_effects.ticking.tickers.WaterfallTicker;
import einstein.subtle_effects.ticking.tickers.geyser.GeyserTicker;
import einstein.subtle_effects.ticking.tickers.geyser.GeyserType;
import einstein.subtle_effects.util.BlockTickerProvider;
import einstein.subtle_effects.util.MathUtil;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class SubtleEffectsDisplayTick {

    private static final int AIR = 1;
    private static final int FIRE = 1 << 1;
    private static final int SPAWNABLE = 1 << 2;
    private static final int CHEST = 1 << 3;
    private static final int GEYSER = 4;

    private static final int KEYED = 1;
    private static final int BLOCKED = 1 << 1;
    private static final int HABITAT = 1 << 2;
    private static final int EFFECTS = 1 << 3;

    private static final GeyserType[] GEYSERS = GeyserType.values();
    private static final BlockTickerProvider[] NO_PROVIDERS = new BlockTickerProvider[0];

    private static final Reference2ObjectOpenHashMap<BlockState, Plan> PLANS = new Reference2ObjectOpenHashMap<>();
    private static final Reference2IntOpenHashMap<Holder<Biome>> BIOMES = new Reference2IntOpenHashMap<>();
    private static final Reference2IntOpenHashMap<FluidState> FLUIDS = new Reference2IntOpenHashMap<>();

    static {
        BIOMES.defaultReturnValue(-1);
        FLUIDS.defaultReturnValue(-1);
    }

    private static final Object[] watched = new Object[13];
    private static final long[] PASS_CHUNK_KEYS = new long[64];
    private static final ChunkAccess[] PASS_CHUNKS = new ChunkAccess[64];
    private static final LevelChunkSection[][] PASS_SECTIONS = new LevelChunkSection[64][];
    private static final BiomeManager.NoiseBiomeSource PASS_SOURCE = SubtleEffectsDisplayTick::passNoiseBiome;
    private static Level passLevel;
    private static BiomeManager passBiomes;
    private static int passMinQuartY;
    private static int passMaxQuartY;
    private static int passMinSection;
    private static RandomSource passRandom;
    private static long passTime;
    private static Object[] specialKeys = new Object[0];
    private static Predicate<BlockState>[] specialTests;
    private static BlockTickerProvider[] specialProviders;

    private static boolean fireflies;
    private static FireflyConfigs.FireflyType fireflyType;
    private static double fireflyHabitatSpawnChance;
    private static double fireflyDefaultSpawnChance;
    private static double fireflyHabitatSoundChance;
    private static double fireflyDefaultSoundChance;
    private static float fireflyVolume;
    private static List<? extends ResourceLocation> fireflyAllowlist = List.of();
    private static boolean night;
    private static boolean onlyHabitat;
    private static boolean effects;
    private static boolean nether;
    private static boolean waterfalls;
    private static List<?> spawnable;
    private static List<?> blocklist;
    private static List<?> habitats;
    private static final List<?>[] effectBiomes = new List<?>[3];
    private static final List<?>[] geyserBlocks = new List<?>[GEYSERS.length];
    private static final double[] geyserChance = new double[GEYSERS.length];

    private SubtleEffectsDisplayTick() {
    }

    public static Holder<Biome> run(Level level, BlockPos.MutableBlockPos cursor, RandomSource random) {
        if (random != passRandom || level != watched[12] || level.getGameTime() != passTime) {
            beginPass(level, random);
        }
        BlockState state = level.getBlockState(cursor);
        Plan plan = plan(state);
        int flags = plan.flags;
        if ((flags & FIRE) != 0 && ModConfigs.ENTITIES.endCrystalsDisableFireEffects) {
            List<EndCrystal> crystals = new ArrayList<>();
            level.getEntities(EntityType.END_CRYSTAL, new AABB(cursor), Predicates.alwaysTrue(), crystals);
            if (!crystals.isEmpty()) {
                return null;
            }
        }
        BlockPos pos = null;
        FluidState fluid = null;
        Holder<Biome> biome = null;
        if ((fireflies || effects) && !state.isCollisionShapeFullBlock(level, cursor)) {
            fluid = level.getFluidState(cursor);
            if (fluid.isEmpty()) {
                biome = passBiomes.getBiome(cursor);
                int info = biomeInfo(biome);
                if (fireflies && firefly(info, flags)) {
                    pos = cursor.immutable();
                    fireflyTick(level, pos, state, random, biome, (info & HABITAT) != 0, (flags & SPAWNABLE) != 0);
                }
                if ((info & EFFECTS) != 0) {
                    if (pos == null) {
                        pos = cursor.immutable();
                    }
                    BiomeEffectsManager.tick(level, pos, state, random);
                }
            }
        }
        if ((flags & AIR) != 0) {
            return biome;
        }
        Block block = state.getBlock();
        BlockTickerProvider ticker = ModBlockTickers.REGISTERED.get(block);
        if (ticker != null) {
            if (pos == null) {
                pos = cursor.immutable();
            }
            ticker.apply(state, level, pos, random);
        }
        if (fluid == null) {
            fluid = level.getFluidState(cursor);
        }
        int geysers = (flags >>> GEYSER) & 7;
        int fluidGeysers = fluidGeysers(fluid);
        if ((geysers | fluidGeysers) != 0) {
            geysers(level, state, pos != null ? pos : cursor, geysers, fluidGeysers);
        }
        if ((flags & CHEST) != 0) {
            if (pos == null) {
                pos = cursor.immutable();
            }
            ChestBlockEntityTicker.trySpawn(level, pos, state);
        }
        if (waterfalls && !fluid.isEmpty() && fluid.is(FluidTags.WATER) && fluid.isSource()) {
            if (pos == null) {
                pos = cursor.immutable();
            }
            WaterfallTicker.trySpawn(level, fluid, pos);
        }
        BlockTickerProvider[] specials = plan.specials;
        if (specials.length != 0) {
            if (pos == null) {
                pos = cursor.immutable();
            }
            for (BlockTickerProvider special : specials) {
                special.apply(state, level, pos, random);
            }
        }
        if (SparkProviderReloadListener.PROVIDERS.get(block) != null) {
            if (pos == null) {
                pos = cursor.immutable();
            }
            SparkProviderManager.tick(level, random, block, state, pos);
        }
        return biome;
    }

    private static Holder<Biome> passNoiseBiome(int quartX, int quartY, int quartZ) {
        int chunkX = QuartPos.toSection(quartX);
        int chunkZ = QuartPos.toSection(quartZ);
        long key = ChunkPos.asLong(chunkX, chunkZ);
        int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 58);
        ChunkAccess chunk;
        LevelChunkSection[] sections;
        if (PASS_CHUNK_KEYS[slot] == key) {
            chunk = PASS_CHUNKS[slot];
            sections = PASS_SECTIONS[slot];
        } else {
            chunk = passLevel.getChunk(chunkX, chunkZ, ChunkStatus.BIOMES, false);
            sections = chunk != null && chunk.getClass() == LevelChunk.class ? chunk.getSections() : null;
            PASS_CHUNK_KEYS[slot] = key;
            PASS_CHUNKS[slot] = chunk;
            PASS_SECTIONS[slot] = sections;
        }
        if (sections != null) {
            int y = Mth.clamp(quartY, passMinQuartY, passMaxQuartY);
            return sections[(y >> 2) - passMinSection].getNoiseBiome(quartX & 3, y & 3, quartZ & 3);
        }
        return chunk != null ? chunk.getNoiseBiome(quartX, quartY, quartZ) : passLevel.getUncachedNoiseBiome(quartX, quartY, quartZ);
    }

    private static boolean firefly(int info, int flags) {
        if ((info & KEYED) == 0 || (info & BLOCKED) != 0) {
            return false;
        }
        return (info & HABITAT) != 0 || !onlyHabitat && (flags & SPAWNABLE) != 0;
    }

    private static void fireflyTick(Level level, BlockPos pos, BlockState state, RandomSource random, Holder<Biome> biome,
                                    boolean habitat, boolean spawnable) {
        boolean spawn = random.nextDouble() < (habitat ? fireflyHabitatSpawnChance : fireflyDefaultSpawnChance);
        boolean sound = fireflyVolume > 0.0F
                && random.nextDouble() < (habitat ? fireflyHabitatSoundChance : fireflyDefaultSoundChance);
        if (!spawn && !sound) {
            return;
        }
        if (!night && level.getBrightness(LightLayer.SKY, pos) != 0 || level.getBrightness(LightLayer.BLOCK, pos) > 5) {
            return;
        }
        boolean canSeeSky = level.canSeeSky(pos);
        int surfaceLevel = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY();
        if (habitat && (!canSeeSky && !spawnable || canSeeSky && surfaceLevel + 10 < pos.getY())) {
            return;
        }
        if (!biome.value().warmEnoughToRain(pos) && !fireflyAllowlist.contains(biome.unwrapKey().get().location())) {
            return;
        }
        if (level.isRaining() && canSeeSky && surfaceLevel <= pos.getY()) {
            return;
        }
        if (spawn) {
            level.addParticle(fireflyType.getParticle().get(),
                    pos.getX() + 0.5 + MathUtil.nextNonAbsDouble(random, 0.4375),
                    pos.getY() + 0.5 + MathUtil.nextNonAbsDouble(random, 0.4375),
                    pos.getZ() + 0.5 + MathUtil.nextNonAbsDouble(random, 0.4375),
                    0.0, 0.0, 0.0);
        }
        if (sound) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.FIREFLY_BUZZ.get(), SoundSource.AMBIENT,
                    fireflyVolume, 1.0F, false);
        }
    }

    private static void geysers(Level level, BlockState state, BlockPos pos, int blocks, int fluids) {
        for (int i = 0; i < GEYSERS.length; i++) {
            if (!((blocks >>> i & 1) != 0 && state.isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER)) && (fluids >>> i & 1) == 0) {
                continue;
            }
            GeyserType type = GEYSERS[i];
            if (type.isNetherOnly && !nether) {
                return;
            }
            long seed = state.getSeed(pos) * (long) (i + 1);
            if (!(LegacySeedDouble.of(seed) < geyserChance[i])) {
                continue;
            }
            RandomSource random = RandomSource.create(seed);
            random.nextDouble();
            GeyserTicker.trySpawn(type, level, pos.immutable(), random);
        }
    }

    private static int fluidGeysers(FluidState fluid) {
        int cached = FLUIDS.getInt(fluid);
        if (cached >= 0) {
            return cached;
        }
        Block block = fluid.createLegacyBlock().getBlock();
        int mask = 0;
        for (int i = 0; i < GEYSERS.length; i++) {
            if (geyserBlocks[i].contains(block)) {
                mask |= 1 << i;
            }
        }
        FLUIDS.put(fluid, mask);
        return mask;
    }

    private static int biomeInfo(Holder<Biome> biome) {
        int cached = BIOMES.getInt(biome);
        if (cached >= 0) {
            return cached;
        }
        int info = 0;
        Optional<ResourceKey<Biome>> key = biome.unwrapKey();
        if (key.isPresent()) {
            ResourceLocation id = key.get().location();
            info = KEYED;
            if (blocklist.contains(id)) {
                info |= BLOCKED;
            }
            if (habitats.contains(id)) {
                info |= HABITAT;
            }
            for (List<?> list : effectBiomes) {
                if (list.contains(id)) {
                    info |= EFFECTS;
                    break;
                }
            }
        }
        BIOMES.put(biome, info);
        return info;
    }

    private static Plan plan(BlockState state) {
        Plan plan = PLANS.get(state);
        if (plan != null) {
            return plan;
        }
        Block block = state.getBlock();
        int flags = 0;
        if (block instanceof BaseFireBlock) {
            flags |= FIRE;
        }
        if (spawnable.contains(block)) {
            flags |= SPAWNABLE;
        }
        BlockTickerProvider[] specials = NO_PROVIDERS;
        if (state.isAir()) {
            flags |= AIR;
        } else {
            for (int i = 0; i < GEYSERS.length; i++) {
                if (geyserBlocks[i].contains(block)) {
                    flags |= 1 << (GEYSER + i);
                }
            }
            if (state.hasBlockEntity() && !(state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) == ChestType.LEFT)) {
                flags |= CHEST;
            }
            int count = 0;
            BlockTickerProvider[] matched = null;
            for (int i = 0; i < specialTests.length; i++) {
                if (specialTests[i].test(state)) {
                    if (matched == null) {
                        matched = new BlockTickerProvider[specialTests.length];
                    }
                    matched[count++] = specialProviders[i];
                }
            }
            if (count != 0) {
                specials = Arrays.copyOf(matched, count);
            }
        }
        plan = new Plan(flags, specials);
        PLANS.put(state, plan);
        return plan;
    }

    @SuppressWarnings("unchecked")
    private static void beginPass(Level level, RandomSource random) {
        passRandom = random;
        passTime = level.getGameTime();
        passLevel = level;
        passBiomes = level.getBiomeManager().withDifferentSource(PASS_SOURCE);
        Arrays.fill(PASS_CHUNK_KEYS, Long.MIN_VALUE);
        Arrays.fill(PASS_CHUNKS, null);
        Arrays.fill(PASS_SECTIONS, null);
        passMinQuartY = QuartPos.fromBlock(level.getMinBuildHeight());
        passMaxQuartY = passMinQuartY + QuartPos.fromBlock(level.getHeight()) - 1;
        passMinSection = level.getMinSection();
        FireflyConfigs config = ModConfigs.ENVIRONMENT.fireflies;
        BiomeConfigs biomes = ModConfigs.ENVIRONMENT.biomes;
        Object[] now = {
                config.spawnableBlocks.get(),
                config.biomesBlocklist.get(),
                config.habitatBiomes.get(),
                biomes.mushroomSporeBiomes.get(),
                biomes.pollenBiomes.get(),
                biomes.sculkDustBiomes.get(),
                GEYSERS[0].spawnableBlocks.get(),
                GEYSERS[1].spawnableBlocks.get(),
                GEYSERS[2].spawnableBlocks.get(),
                ModConfigs.BLOCKS.redstoneDustEmittingBlocks.get(),
                ModConfigs.BLOCKS.glowstoneDustEmittingBlocks.get(),
                ModConfigs.BLOCKS.amethystSparkleEmittingBlocks.get(),
                level
        };
        boolean stale = false;
        for (int i = 0; i < now.length && !stale; i++) {
            stale = now[i] != watched[i];
        }
        Map<Predicate<BlockState>, BlockTickerProvider> special = ModBlockTickers.REGISTERED_SPECIAL;
        Object[] keys = new Object[special.size() * 2];
        int k = 0;
        for (Map.Entry<Predicate<BlockState>, BlockTickerProvider> entry : special.entrySet()) {
            keys[k++] = entry.getKey();
            keys[k++] = entry.getValue();
        }
        if (stale || !Arrays.equals(keys, specialKeys)) {
            System.arraycopy(now, 0, watched, 0, now.length);
            specialKeys = keys;
            int n = keys.length / 2;
            Predicate<BlockState>[] tests = new Predicate[n];
            BlockTickerProvider[] providers = new BlockTickerProvider[n];
            for (int i = 0; i < n; i++) {
                tests[i] = (Predicate<BlockState>) keys[2 * i];
                providers[i] = (BlockTickerProvider) keys[2 * i + 1];
            }
            specialTests = tests;
            specialProviders = providers;
            spawnable = (List<?>) now[0];
            blocklist = (List<?>) now[1];
            habitats = (List<?>) now[2];
            for (int i = 0; i < 3; i++) {
                effectBiomes[i] = (List<?>) now[3 + i];
            }
            for (int i = 0; i < GEYSERS.length; i++) {
                geyserBlocks[i] = (List<?>) now[6 + i];
            }
            PLANS.clear();
            BIOMES.clear();
            FLUIDS.clear();
        }
        effects = !effectBiomes[0].isEmpty() || !effectBiomes[1].isEmpty() || !effectBiomes[2].isEmpty();
        onlyHabitat = config.onlyAllowInHabitatBiomes.get();
        float time = (float) level.getDayTime() % 24000.0F;
        night = time > 13000.0F && time < 23000.0F;
        nether = level.dimension().equals(Level.NETHER);
        waterfalls = ModConfigs.ENVIRONMENT.waterfalls.waterfallsEnabled.get();
        for (int i = 0; i < GEYSERS.length; i++) {
            geyserChance[i] = 1.0E-4 * (double) GEYSERS[i].spawnChance.get();
        }
        fireflies = fireflyGate(level, config);
        if (fireflies) {
            int habitatDensity = config.habitatBiomeDensity.get();
            int defaultDensity = config.defaultDensity.get();
            fireflyType = config.fireflyType.get();
            int spawnRate = fireflyType == FireflyConfigs.FireflyType.VANILLA ? 170 : 1;
            fireflyHabitatSpawnChance = 5.0E-4 * (double) habitatDensity;
            fireflyDefaultSpawnChance = 0.008 * (double) defaultDensity * (double) spawnRate;
            fireflyHabitatSoundChance = 1.0E-5 * (double) habitatDensity;
            fireflyDefaultSoundChance = 3.0E-4 * (double) defaultDensity;
            fireflyVolume = config.fireflySoundVolume.get();
            fireflyAllowlist = config.biomesAllowlist.get();
        }
    }

    private static boolean fireflyGate(Level level, FireflyConfigs config) {
        if (!config.firefliesEnabled.get() || (habitats.isEmpty() && spawnable.isEmpty())) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.tickCount < 200) {
            return false;
        }
        Optional<ResourceKey<DimensionType>> dimension = level.dimensionTypeRegistration().unwrapKey();
        if (dimension.isEmpty() || config.dimensionBlocklist.get().contains(dimension.get().location())) {
            return false;
        }
        return !CompatHelper.IS_SERENE_SEANSONS_LOADED.get() || !SereneSeasonsCompat.isColdSeason(level, config.ignoredSeasons.get());
    }

    private static final class Plan {

        final int flags;
        final BlockTickerProvider[] specials;

        Plan(int flags, BlockTickerProvider[] specials) {
            this.flags = flags;
            this.specials = specials;
        }
    }
}
