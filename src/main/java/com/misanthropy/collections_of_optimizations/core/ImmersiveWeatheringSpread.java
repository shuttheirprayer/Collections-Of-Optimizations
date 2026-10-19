package com.misanthropy.collections_of_optimizations.core;

import com.ordana.immersive_weathering.blocks.cracked.Crackable;
import com.ordana.immersive_weathering.blocks.mossy.Mossable;
import com.ordana.immersive_weathering.configs.CommonConfigs;
import com.ordana.immersive_weathering.data.block_growths.growths.ConfigurableBlockGrowth;
import com.ordana.immersive_weathering.reg.ModTags;
import com.ordana.immersive_weathering.util.PatchSpreader;
import com.ordana.immersive_weathering.util.Weatherable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;
import java.util.Set;

public final class ImmersiveWeatheringSpread {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long MASK = (1L << 48) - 1;

    private static final int NONE = 0;
    private static final int WEATHER = 1;
    private static final int PREVENT = 2;
    private static final int ALWAYS = 3;

    private static final int LOW = 1;
    private static final int MEDIUM = 2;
    private static final int HIGH = 3;

    private static final int IDENTITY = 0 | 1 << 3 | 2 << 6 | 3 << 9 | 4 << 12 | 5 << 15;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final BlockState VOID_AIR = Blocks.VOID_AIR.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private static final int MAX_RADIUS = 16;
    private static final int OFFSET_BIAS = 512;

    private static final ThreadLocal<AreaScratch> AREA = ThreadLocal.withInitial(AreaScratch::new);

    private ImmersiveWeatheringSpread() {
    }

    public static long scramble(long seed) {
        return (seed ^ MULTIPLIER) & MASK;
    }

    public static long step(long seed) {
        return seed * MULTIPLIER + 0xBL & MASK;
    }

    public static float toFloat(long seed) {
        return (int) (seed >>> 24) / (float) (1 << 24);
    }

    public static boolean moss(boolean hasTicked, BlockPos pos, Level level, int maxRecursion) {
        return wanted(false, hasTicked, pos, level, maxRecursion,
                CommonConfigs.MOSS_INTERESTS_FOR_FACE.get(), CommonConfigs.MOSS_PATCHINESS.get(),
                CommonConfigs.MOSS_IMMUNE_CHANCE.get(), CommonConfigs.MOSS_NEEDS_AIR.get());
    }

    public static boolean crack(boolean hasTicked, BlockPos pos, Level level, int maxRecursion) {
        return wanted(true, hasTicked, pos, level, maxRecursion,
                CommonConfigs.CRACK_INTERESTS_FOR_FACE.get(), CommonConfigs.CRACK_PATCHINESS.get(),
                CommonConfigs.CRACK_IMMUNE_CHANCE.get(), CommonConfigs.CRACK_NEEDS_AIR.get());
    }

    private static boolean wanted(boolean crack, boolean hasTicked, BlockPos pos, Level level, int maxRecursion,
                                  double directionChance, double highChance, double immuneChance, boolean needsAir) {
        long seed = scramble(Mth.getSeed(pos));
        seed = step(seed);
        int wantedDirs = directionCount(toFloat(seed), (float) directionChance);
        int perm = IDENTITY;
        for (int i = 6; i > 1; i--) {
            seed = step(seed);
            int j = (int) (seed >>> 17);
            int m = i - 1;
            if ((i & m) == 0) {
                j = (int) ((i * (long) j) >> 31);
            } else {
                for (int u = j; u - (j = u % i) + m < 0; u = (int) (seed >>> 17)) {
                    seed = step(seed);
                }
            }
            int a = perm >>> 3 * (i - 1) & 7;
            int b = perm >>> 3 * j & 7;
            perm = perm & ~(7 << 3 * (i - 1)) & ~(7 << 3 * j) | b << 3 * (i - 1) | a << 3 * j;
        }
        int sus = 0;
        for (int i = 0; i < 6; i++) {
            int susceptibility;
            if (i < wantedDirs) {
                seed = step(seed);
                susceptibility = (double) toFloat(seed) < highChance ? HIGH : MEDIUM;
            } else {
                susceptibility = LOW;
            }
            sus |= susceptibility << 2 * (perm >>> 3 * i & 7);
        }
        if (hasTicked) {
            seed = step(seed);
            if ((double) toFloat(seed) < immuneChance) {
                return false;
            }
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean hasAir = false;
        boolean any = false;
        boolean always = false;
        for (int d = 0; d < 6; d++) {
            cursor.setWithOffset(pos, DIRECTIONS[d]);
            BlockState state = level.getBlockState(cursor);
            if (needsAir && !hasAir) {
                hasAir = !state.isRedstoneConductor(level, pos);
            }
            int effect = effect(crack, sus >>> 2 * d & 3, state, level, cursor, maxRecursion);
            if (effect == PREVENT) {
                return false;
            }
            if (effect != NONE) {
                any = true;
                if (effect == ALWAYS) {
                    always = true;
                }
            }
        }
        if (needsAir && !always && !hasAir) {
            return false;
        }
        return any;
    }

    private static int directionCount(float r, float a) {
        for (int x = 0; x <= 6; ++x) {
            float value = -0.023809524f * ((float) x * (12.0f * a - 2.0f * a - 12.0f + 1.0f) + (float) (x * x) * (1.0f - 2.0f * a) + (2.0f * a * 6.0f - 12.0f));
            if (r < value) {
                return x;
            }
        }
        return 0;
    }

    private static int effect(boolean crack, int sus, BlockState state, Level level, BlockPos.MutableBlockPos cursor, int maxRecursion) {
        if (sus >= MEDIUM && state.is(crack ? ModTags.CRACKED : ModTags.MOSSY)) {
            return WEATHER;
        }
        if (sus == HIGH && maxRecursion > 0 && state.getBlock() instanceof Weatherable weatherable && weatherable.isWeathering(state)) {
            PatchSpreader<?> spreader = crack
                    ? weatherable.getPatchSpreader(Crackable.CrackLevel.class).orElse(null)
                    : weatherable.getPatchSpreader(Mossable.MossLevel.class).orElse(null);
            if (spreader != null && spreader.getWantedWeatheringState(false, cursor.immutable(), level, maxRecursion - 1)) {
                return WEATHER;
            }
        }
        FluidState fluid = state.getFluidState();
        if (crack) {
            if (fluid.is(FluidTags.LAVA) || state.getBlock() instanceof FireBlock || state.is(ModTags.CRACK_SOURCE)) {
                return ALWAYS;
            }
            return NONE;
        }
        if (fluid.is(FluidTags.LAVA)) {
            return PREVENT;
        }
        if (state.is(ModTags.MOSS_SOURCE) || fluid.is(FluidTags.WATER)) {
            return ALWAYS;
        }
        return NONE;
    }

    public static boolean fits(int rx, int ry, int rz) {
        return rx >= 0 && ry >= 0 && rz >= 0 && rx <= MAX_RADIUS && ry <= MAX_RADIUS && rz <= MAX_RADIUS;
    }

    public static boolean area(BlockPos originalPos, Level level, ConfigurableBlockGrowth config, int rx, int ry, int rz, int requiredAmount,
                               Optional<Integer> yOffset, Optional<RuleTest> mustHave, Optional<RuleTest> mustNotHave,
                               Optional<HolderSet<Block>> included) {
        BlockPos pos = yOffset.isPresent() ? originalPos.above(yOffset.get()) : originalPos;
        AreaScratch scratch = AREA.get();
        long posSeed = Mth.getSeed(pos);
        int size = scratch.shuffle(rx, ry, rz, posSeed);
        int[] order = scratch.order;
        LegacyRandomSource random = scratch.random;
        random.setSeed(posSeed);
        RuleTest have = mustHave.orElse(null);
        RuleTest notHave = mustNotHave.orElse(null);
        HolderSet<Block> extra = included.orElse(null);
        Set<Block> possible = config.getPossibleBlocks();
        boolean hasRequirement = have == null;
        boolean server = level instanceof ServerLevel && !level.isDebug();
        BlockPos.MutableBlockPos cursor = scratch.cursor;
        int px = pos.getX();
        int py = pos.getY();
        int pz = pos.getZ();
        LevelChunk chunk = null;
        int chunkX = 0;
        int chunkZ = 0;
        LevelChunkSection section = null;
        int sectionKey = Integer.MIN_VALUE;
        int count = 0;
        for (int i = 0; i < size; i++) {
            int offset = order[i];
            int x = px + (offset >>> 20) - OFFSET_BIAS;
            int y = py + (offset >>> 10 & 1023) - OFFSET_BIAS;
            int z = pz + (offset & 1023) - OFFSET_BIAS;
            cursor.set(x, y, z);
            BlockState state;
            if (!server) {
                state = level.getBlockState(cursor);
            } else if (level.isOutsideBuildHeight(y)) {
                state = VOID_AIR;
            } else {
                int cx = x >> 4;
                int cz = z >> 4;
                if (chunk == null || cx != chunkX || cz != chunkZ) {
                    chunk = level.getChunk(cx, cz);
                    chunkX = cx;
                    chunkZ = cz;
                    sectionKey = Integer.MIN_VALUE;
                }
                int index = chunk.getSectionIndex(y);
                if (index != sectionKey) {
                    sectionKey = index;
                    LevelChunkSection[] sections = chunk.getSections();
                    section = index >= 0 && index < sections.length && !sections[index].hasOnlyAir() ? sections[index] : null;
                }
                state = section == null ? AIR : section.getBlockState(x & 15, y & 15, z & 15);
            }
            if (possible.contains(state.getBlock()) || extra != null && state.is(extra)) {
                ++count;
            }
            if (!hasRequirement && have.test(state, random)) {
                hasRequirement = true;
                if (requiredAmount == -1) {
                    break;
                }
            } else if (notHave != null && notHave.test(state, random)) {
                return false;
            }
            if (count >= requiredAmount) {
                return false;
            }
        }
        return hasRequirement;
    }

    private static final class AreaScratch {
        private final Int2ObjectOpenHashMap<int[]> templates = new Int2ObjectOpenHashMap<>();
        private final LegacyRandomSource random = new LegacyRandomSource(0L);
        private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        private int[] order = new int[0];
        private int lastKey = -1;
        private int[] lastTemplate;

        int shuffle(int rx, int ry, int rz, long posSeed) {
            int key = rx | ry << 10 | rz << 20;
            int[] template = this.lastTemplate;
            if (key != this.lastKey) {
                template = this.templates.get(key);
                if (template == null) {
                    template = template(rx, ry, rz);
                    this.templates.put(key, template);
                }
                this.lastKey = key;
                this.lastTemplate = template;
            }
            int size = template.length;
            if (this.order.length < size) {
                this.order = new int[size];
            }
            int[] order = this.order;
            System.arraycopy(template, 0, order, 0, size);
            long seed = scramble(posSeed);
            for (int i = size; i > 1; i--) {
                seed = step(seed);
                int r = (int) (seed >>> 17);
                int m = i - 1;
                if ((i & m) == 0) {
                    r = (int) ((i * (long) r) >> 31);
                } else {
                    for (int u = r; u - (r = u % i) + m < 0; u = (int) (seed >>> 17)) {
                        seed = step(seed);
                    }
                }
                int value = order[m];
                order[m] = order[r];
                order[r] = value;
            }
            return size;
        }
    }

    private static int[] template(int rx, int ry, int rz) {
        int[] values = new int[(2 * rx + 1) * (2 * ry + 1) * (2 * rz + 1)];
        int cursor = 0;
        int total = rx + ry + rz;
        for (int depth = 0; depth <= total; depth++) {
            int maxX = Math.min(rx, depth);
            for (int x = -maxX; x <= maxX; x++) {
                int maxY = Math.min(ry, depth - Math.abs(x));
                for (int y = -maxY; y <= maxY; y++) {
                    int z = depth - Math.abs(x) - Math.abs(y);
                    if (z > rz) {
                        continue;
                    }
                    values[cursor++] = pack(x, y, z);
                    if (z != 0) {
                        values[cursor++] = pack(x, y, -z);
                    }
                }
            }
        }
        if (cursor != values.length) {
            throw new IllegalStateException("Area template " + rx + "x" + ry + "x" + rz + " filled " + cursor + " of " + values.length);
        }
        return values;
    }

    private static int pack(int x, int y, int z) {
        return x + OFFSET_BIAS << 20 | y + OFFSET_BIAS << 10 | z + OFFSET_BIAS;
    }
}
