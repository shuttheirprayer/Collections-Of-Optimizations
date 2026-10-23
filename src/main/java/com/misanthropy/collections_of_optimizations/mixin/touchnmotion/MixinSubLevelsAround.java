package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import strm.touchnmotion.interaction.SubLevels;

@Mixin(value = SubLevels.class, remap = false)
public abstract class MixinSubLevelsAround {

    @Unique
    private static final List<SubLevels.Space> coo$WORLD_ONLY = List.of(SubLevels.WORLD);

    @Shadow
    private static Object helper;
    @Shadow
    private static Method intersecting;
    @Shadow
    private static Constructor<?> box;

    @Shadow
    private static boolean ready() {
        throw new AssertionError();
    }

    @Shadow
    private static Object call(Method method, Object target, Object... args) {
        throw new AssertionError();
    }

    @Shadow
    private static Object newInstance(Constructor<?> constructor, Object... args) {
        throw new AssertionError();
    }

    @Shadow
    private static SubLevels.Space space(Object sub) {
        throw new AssertionError();
    }

    /**
     * @author Misanthropy
     * @reason opt
     */
    @Overwrite
    public static List<SubLevels.Space> around(Level level, AABB area) {
        boolean sable = ready();
        if (!sable && CoOConfig.touchnmotionSharedWorldSpace) return coo$WORLD_ONLY;
        ArrayList<SubLevels.Space> spaces = new ArrayList<>(2);
        spaces.add(SubLevels.WORLD);
        if (!sable) return spaces;
        Object bounds = newInstance(box, area);
        for (Object sub : (Iterable<?>) call(intersecting, helper, level, bounds)) spaces.add(space(sub));
        return spaces;
    }
}
