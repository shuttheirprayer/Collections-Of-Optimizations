package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;

public final class DeeperDarkerRefs {

    private static final String JAW_CLASS = "com.kyanite.deeperdarker.content.blocks.SculkJawBlock";
    private static final String MOB_TYPE_CLASS = "com.kyanite.deeperdarker.content.entities.DDMobType";

    private static final ThreadLocal<RandomSource> SCRATCH_RANDOM = ThreadLocal.withInitial(RandomSource::create);

    private static final Object LOCK = new Object();

    private static volatile boolean blocksResolved;
    private static volatile boolean blocksUsable;
    private static Block crystallizedAmber;
    private static Block iceLily;
    private static Block ancientVase;

    private static volatile boolean jawResolved;
    private static volatile boolean jawUsable;
    private static BooleanProperty jawBiting;
    private static BooleanProperty jawCanBite;
    private static MobType sculkMobType;

    private DeeperDarkerRefs() {
    }

    public static RandomSource scratchRandom() {
        return SCRATCH_RANDOM.get();
    }

    public static boolean mayNeedBreakEvent(Block block) {
        if (!blocksResolved) {
            resolveBlocks();
        }
        if (!blocksUsable) {
            return true;
        }
        return block == crystallizedAmber || block == iceLily || block == ancientVase;
    }

    public static boolean jawRefsUsable() {
        if (!jawResolved) {
            resolveJaw();
        }
        return jawUsable;
    }

    public static BooleanProperty jawBiting() {
        return jawBiting;
    }

    public static BooleanProperty jawCanBite() {
        return jawCanBite;
    }

    public static MobType sculkMobType() {
        return sculkMobType;
    }

    private static void resolveBlocks() {
        synchronized (LOCK) {
            if (blocksResolved) {
                return;
            }
            try {
                crystallizedAmber = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("deeperdarker", "crystallized_amber"));
                iceLily = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("deeperdarker", "ice_lily"));
                ancientVase = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("deeperdarker", "ancient_vase"));
                blocksUsable = crystallizedAmber != null && iceLily != null && ancientVase != null;
            } catch (Throwable throwable) {
                blocksUsable = false;
            }
            blocksResolved = true;
        }
    }

    private static void resolveJaw() {
        synchronized (LOCK) {
            if (jawResolved) {
                return;
            }
            try {
                Class<?> jaw = Class.forName(JAW_CLASS, false, DeeperDarkerRefs.class.getClassLoader());
                Field biting = jaw.getField("BITING");
                Field canBite = jaw.getField("CAN_BITE");
                Class<?> mobTypes = Class.forName(MOB_TYPE_CLASS, false, DeeperDarkerRefs.class.getClassLoader());
                Field sculk = mobTypes.getField("SCULK");
                Object bitingValue = biting.get(null);
                Object canBiteValue = canBite.get(null);
                Object sculkValue = sculk.get(null);
                if (bitingValue instanceof BooleanProperty bitingProperty
                        && canBiteValue instanceof BooleanProperty canBiteProperty
                        && sculkValue instanceof MobType mobType) {
                    jawBiting = bitingProperty;
                    jawCanBite = canBiteProperty;
                    sculkMobType = mobType;
                    jawUsable = true;
                }
            } catch (Throwable throwable) {
                jawUsable = false;
            }
            jawResolved = true;
        }
    }
}
