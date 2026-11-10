package com.misanthropy.collections_of_optimizations.mixin.cablefacades;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CableFacadeServerData;
import com.portingdeadmods.cable_facades.data.FacadeData;
import com.portingdeadmods.cable_facades.utils.FacadeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FacadeUtils.class, remap = false)
public abstract class MixinFacadeUtilsServerLookup {

    @Inject(method = "getFacadeData", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$serverLookup(BlockGetter level, BlockPos pos, CallbackInfoReturnable<FacadeData> cir) {
        if (CoOConfig.cablefacadesCacheServerFacadeData && pos != null && level instanceof ServerLevel serverLevel) {
            cir.setReturnValue(CableFacadeServerData.lookup(serverLevel, pos));
        }
    }
}
