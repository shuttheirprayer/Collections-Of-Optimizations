package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringSpread;
import com.ordana.immersive_weathering.blocks.mossy.MossSpreader;
import com.ordana.immersive_weathering.blocks.mossy.Mossable;
import com.ordana.immersive_weathering.util.PatchSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MossSpreader.class, remap = false)
public abstract class MixinMossSpreader implements PatchSpreader<Mossable.MossLevel> {

    @Override
    public boolean getWantedWeatheringState(boolean hasTicked, BlockPos pos, Level level, int maxRecursion) {
        if (!CoOConfig.immersiveweatheringFastSpread) {
            return PatchSpreader.super.getWantedWeatheringState(hasTicked, pos, level, maxRecursion);
        }
        return ImmersiveWeatheringSpread.moss(hasTicked, pos, level, maxRecursion);
    }
}
