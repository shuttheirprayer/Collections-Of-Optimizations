package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(MultifaceBlock.class)
public abstract class MixinMultifaceBlockFaces {

    @Shadow
    @Final
    protected static Direction[] DIRECTIONS;

    @Shadow
    public static boolean hasFace(BlockState state, Direction direction) {
        throw new AssertionError();
    }

    @Redirect(method = "hasAnyFace", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;stream([Ljava/lang/Object;)Ljava/util/stream/Stream;"), require = 0)
    private static Stream<Direction> coo$anyFaceStream(Object[] directions, BlockState state) {
        return CoOConfig.vanillaMultifaceWithoutStreams ? null : Arrays.stream(DIRECTIONS);
    }

    @Redirect(method = "hasAnyFace", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"), require = 0)
    private static boolean coo$anyFace(Stream<Direction> stream, Predicate<Direction> test, BlockState state) {
        if (stream != null) return stream.anyMatch(test);
        for (Direction direction : DIRECTIONS) {
            if (hasFace(state, direction)) return true;
        }
        return false;
    }

    @Redirect(method = "hasAnyVacantFace", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;stream([Ljava/lang/Object;)Ljava/util/stream/Stream;"), require = 0)
    private static Stream<Direction> coo$vacantFaceStream(Object[] directions, BlockState state) {
        return CoOConfig.vanillaMultifaceWithoutStreams ? null : Arrays.stream(DIRECTIONS);
    }

    @Redirect(method = "hasAnyVacantFace", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"), require = 0)
    private static boolean coo$vacantFace(Stream<Direction> stream, Predicate<Direction> test, BlockState state) {
        if (stream != null) return stream.anyMatch(test);
        for (Direction direction : DIRECTIONS) {
            if (!hasFace(state, direction)) return true;
        }
        return false;
    }
}
