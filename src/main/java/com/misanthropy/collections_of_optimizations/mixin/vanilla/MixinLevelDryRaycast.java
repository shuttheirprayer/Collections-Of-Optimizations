package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LeanRay;
import com.misanthropy.collections_of_optimizations.core.ProbeCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class MixinLevelDryRaycast implements BlockGetter {

    @Override
    public BlockHitResult clip(ClipContext context) {
        if (CoOConfig.vanillaDryRaycast
                && context.getClass() == ClipContext.class
                && ((ClipContextFluidAccessor) context).coo$fluidMode() == ClipContext.Fluid.NONE) {
            if (CoOConfig.vanillaLeanRaycast) {
                Vec3 from = context.getFrom();
                Vec3 to = context.getTo();
                if (LeanRay.handles(from.x, from.y, from.z, to.x, to.y, to.z)) {
                    LeanRay ray = LeanRay.get();
                    BlockHitResult fast = ray.cast(this, context, null, true, from.x, from.y, from.z, to.x, to.y, to.z)
                            ? ray.result() : coo$miss(context);
                    if (ProbeCheck.ON) {
                        ProbeCheck.compare("Level.clip", fast, BlockGetter.traverseBlocks(from, to, context, this::coo$dryStep, MixinLevelDryRaycast::coo$miss));
                    }
                    return fast;
                }
            }
            return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, this::coo$dryStep, MixinLevelDryRaycast::coo$miss);
        }
        return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, this::coo$stockStep, MixinLevelDryRaycast::coo$miss);
    }

    @Unique
    private BlockHitResult coo$dryStep(ClipContext context, BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        VoxelShape shape = context.getBlockShape(state, this, pos);
        return this.clipWithInteractionOverride(context.getFrom(), context.getTo(), pos, shape, state);
    }

    @Unique
    private BlockHitResult coo$stockStep(ClipContext context, BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        FluidState fluid = this.getFluidState(pos);
        Vec3 from = context.getFrom();
        Vec3 to = context.getTo();
        VoxelShape shape = context.getBlockShape(state, this, pos);
        BlockHitResult blockHit = this.clipWithInteractionOverride(from, to, pos, shape, state);
        VoxelShape fluidShape = context.getFluidShape(fluid, this, pos);
        BlockHitResult fluidHit = fluidShape.clip(from, to, pos);
        double blockDistance = blockHit == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockHit.getLocation());
        double fluidDistance = fluidHit == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(fluidHit.getLocation());
        return blockDistance <= fluidDistance ? blockHit : fluidHit;
    }

    @Unique
    private static BlockHitResult coo$miss(ClipContext context) {
        Vec3 back = context.getFrom().subtract(context.getTo());
        return BlockHitResult.miss(context.getTo(), Direction.getNearest(back.x, back.y, back.z), BlockPos.containing(context.getTo()));
    }
}
