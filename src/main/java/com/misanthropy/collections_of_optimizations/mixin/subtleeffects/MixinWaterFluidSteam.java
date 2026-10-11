package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import einstein.subtle_effects.configs.blocks.SteamConfigs;
import einstein.subtle_effects.init.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaterFluid.class, priority = 1500)
public abstract class MixinWaterFluidSteam {

    @TargetHandler(
            mixin = "einstein.subtle_effects.mixin.client.fluid.WaterFluidMixin",
            name = "animateTick"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$skipWithoutSteam(Level level, BlockPos pos, FluidState state, RandomSource random, CallbackInfo ci, Operation<Void> original) {
        if (CoOConfig.subtleeffectsIdleFluidHooks) {
            SteamConfigs steam = ModConfigs.BLOCKS.steam;
            if (!steam.steamingWater.get() && !steam.boilingWater.get()) {
                return;
            }
        }
        original.call(level, pos, state, random, ci);
    }
}
