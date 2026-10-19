package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringGrowths;
import com.ordana.immersive_weathering.data.block_growths.BlockGrowthHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevelGrowthTick {

    @ModifyExpressionValue(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=randomTick"))
    )
    private FluidState coo$growthTick(FluidState fluid, @Local LevelChunkSection section, @Local BlockPos pos) {
        ImmersiveWeatheringGrowths.randomTick(section, pos, (ServerLevel) (Object) this);
        return fluid;
    }

    @Inject(method = "tickChunk", at = @At("TAIL"))
    private void coo$skyTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        BlockGrowthHandler.performSkyAccessTick((ServerLevel) (Object) this, chunk, randomTickSpeed);
    }
}
