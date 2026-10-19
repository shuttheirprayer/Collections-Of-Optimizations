package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.google.common.collect.ImmutableList;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringFluids;
import com.mojang.datafixers.util.Pair;
import com.ordana.immersive_weathering.data.fluid_generators.FluidGeneratorsHandler;
import com.ordana.immersive_weathering.data.fluid_generators.IFluidGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = FluidGeneratorsHandler.class, remap = false)
public abstract class MixinFluidGeneratorsHandler {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private static void coo$leanGenerate(List<Direction> flowDirections, BlockPos pos, Level level, ImmutableList<IFluidGenerator> generators,
                                         CallbackInfoReturnable<Optional<Pair<BlockPos, SoundEvent>>> cir) {
        if (!CoOConfig.immersiveweatheringLeanFluidGenerators) {
            return;
        }
        if (generators == null) {
            cir.setReturnValue(Optional.empty());
        } else if (ImmersiveWeatheringFluids.handles(generators)) {
            cir.setReturnValue(ImmersiveWeatheringFluids.generate(flowDirections, pos, level, generators));
        }
    }
}
