package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2library.util.math.MathHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class FtInfernalRange {

    public static final double CAP = 256.0D;

    private static final UUID MODIFIER = MathHelper.getUUIDFromString("fallen_traits_infernal_follow_range");

    private FtInfernalRange() {
    }

    public static double clamp(Mob mob, double range) {
        if (!(range > CAP) || !CoOConfig.fallentraitsClampInfernalFollowRange || mob == null) {
            return range;
        }
        AttributeInstance instance = mob.getAttribute(Attributes.FOLLOW_RANGE);
        return instance != null && instance.getModifier(MODIFIER) != null ? CAP : range;
    }
}
