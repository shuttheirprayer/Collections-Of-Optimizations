package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringLeafTint;
import com.ordana.immersive_weathering.ImmersiveWeatheringClient;
import com.ordana.immersive_weathering.blocks.LeafPileBlock;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ImmersiveWeatheringClient.class, remap = false)
public abstract class MixinLeafPileTint {

    @Inject(method = "getLeafTypeColor", at = @At("HEAD"), cancellable = true)
    private static void coo$cachedFade(ClientHelper.BlockColorEvent event, LeavesType type, BlockState state, BlockAndTintGetter level,
                                       BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.immersiveweatheringLeafPileTint) {
            int original = event.getColor(type.leaves.defaultBlockState(), level, pos, tintIndex);
            cir.setReturnValue(ImmersiveWeatheringLeafTint.faded(original, state.getValue(LeafPileBlock.AGE)));
        }
    }
}
