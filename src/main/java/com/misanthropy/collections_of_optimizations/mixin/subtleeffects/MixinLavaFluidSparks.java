package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import einstein.subtle_effects.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LavaFluid.class, priority = 1500)
public abstract class MixinLavaFluidSparks {

    @TargetHandler(
            mixin = "einstein.subtle_effects.mixin.client.fluid.LavaFluidMixin",
            name = "animateTick"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$skipCoveredLava(Level level, BlockPos pos, FluidState state, RandomSource random, CallbackInfo ci, Operation<Void> original) {
        if (CoOConfig.subtleeffectsIdleFluidHooks
                && !(state.hasProperty(FlowingFluid.FALLING) && state.getValue(FlowingFluid.FALLING))
                && ((Fluid) (Object) this).isSame(level.getFluidState(pos).getType())
                && !level.getBlockState(pos).isSolidRender(level, pos)
                && Util.isSolidOrNotEmpty(level, pos.above())) {
            return;
        }
        original.call(level, pos, state, random, ci);
    }
}
