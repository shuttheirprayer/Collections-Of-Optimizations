package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SectionPrecheck;
import com.misanthropy.collections_of_optimizations.core.VanillaOverrideChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MoveToBlockGoal.class)
public abstract class MixinRemoveBlockGoalPrecheck {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Shadow
    @Final
    private int searchRange;

    @Shadow
    @Final
    private int verticalSearchRange;

    @Shadow
    protected int verticalSearchStart;

    @WrapMethod(method = "findNearestBlock", require = 0)
    private boolean coo$skipScanWithoutTarget(Operation<Boolean> original) {
        if (CoOConfig.vanillaPrecheckBlockRemovalScan
                && (Object) this instanceof RemoveBlockGoal
                && VanillaOverrideChecks.stockRemovalTarget(this.getClass())) {
            Level level = this.mob.level();
            if (!level.isDebug()) {
                Block target = ((RemoveBlockGoalAccessor) this).coo$blockToRemove();
                BlockPos pos = this.mob.blockPosition();
                int horizontal = this.searchRange - 1;
                int vertical = Math.max(this.verticalSearchRange, Math.abs(this.verticalSearchStart));
                if (!SectionPrecheck.mayContain(level,
                        pos.getX() - horizontal, pos.getY() - 1 - vertical, pos.getZ() - horizontal,
                        pos.getX() + horizontal, pos.getY() - 1 + vertical, pos.getZ() + horizontal,
                        state -> state.is(target))) {
                    return false;
                }
            }
        }
        return original.call();
    }
}
