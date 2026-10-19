package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.google.gson.JsonElement;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringGrowths;
import com.ordana.immersive_weathering.data.block_growths.BlockGrowthHandler;
import com.ordana.immersive_weathering.data.block_growths.TickSource;
import com.ordana.immersive_weathering.data.block_growths.growths.IBlockGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(value = BlockGrowthHandler.class, remap = false)
public abstract class MixinBlockGrowthHandler {

    @Shadow @Final private static Map<TickSource, Map<Block, Set<IBlockGrowth>>> GROWTH_FOR_BLOCK;
    @Shadow @Final private static Map<TickSource, Set<IBlockGrowth>> UNIVERSAL_GROWTHS;

    @Inject(method = "parse", at = @At("TAIL"))
    private void coo$buildGrowthTable(Map<ResourceLocation, JsonElement> jsonMap, RegistryAccess registryAccess, CallbackInfo ci) {
        ImmersiveWeatheringGrowths.rebuild(GROWTH_FOR_BLOCK, UNIVERSAL_GROWTHS);
    }

    @Inject(method = "tickBlock", at = @At("HEAD"), cancellable = true)
    private static void coo$tickFromTable(TickSource source, BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (CoOConfig.immersiveweatheringGrowthTable && ImmersiveWeatheringGrowths.ready()) {
            ImmersiveWeatheringGrowths.tick(source, state, level, pos);
            ci.cancel();
        }
    }

    @Inject(method = "performSkyAccessTick", at = @At("HEAD"), cancellable = true)
    private static void coo$skyTickFromTable(ServerLevel level, LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (CoOConfig.immersiveweatheringGrowthTable && ImmersiveWeatheringGrowths.ready()) {
            ImmersiveWeatheringGrowths.skyTick(level, chunk, randomTickSpeed);
            ci.cancel();
        }
    }
}
