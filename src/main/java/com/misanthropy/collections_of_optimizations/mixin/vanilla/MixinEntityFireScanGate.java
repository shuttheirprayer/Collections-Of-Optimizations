package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.VanillaOverrideChecks;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntityFireScanGate {

    @Shadow
    private int remainingFireTicks;

    @Shadow
    public boolean wasOnFire;

    @Shadow
    protected abstract int getFireImmuneTicks();

    @WrapOperation(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockStatesIfLoaded(Lnet/minecraft/world/phys/AABB;)Ljava/util/stream/Stream;"
            ),
            require = 0
    )
    private Stream<BlockState> coo$skipIrrelevantFireScan(Level level, AABB box, Operation<Stream<BlockState>> original,
                                                         @Share("coo$fireScanSkipped") LocalBooleanRef skipped) {
        if (CoOConfig.vanillaSkipIrrelevantFireScan && !this.wasOnFire) {
            int ticks = this.remainingFireTicks;
            if (ticks > 0 || ticks == -this.getFireImmuneTicks() && VanillaOverrideChecks.stockFireSetter(this.getClass())) {
                skipped.set(true);
                return Stream.empty();
            }
        }
        return original.call(level, box);
    }

    @WrapOperation(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;noneMatch(Ljava/util/function/Predicate;)Z",
                    remap = false
            ),
            require = 0
    )
    private boolean coo$answerSkippedFireScan(Stream<BlockState> states, Predicate<? super BlockState> test, Operation<Boolean> original,
                                              @Share("coo$fireScanSkipped") LocalBooleanRef skipped) {
        if (skipped.get()) {
            return false;
        }
        return original.call(states, test);
    }
}
