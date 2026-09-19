package com.misanthropy.collections_of_optimizations.mixin.bmd;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BmdBlockCacheState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cerbon.bosses_of_mass_destruction.block.custom.MobWardBlock", remap = false)
public abstract class MixinBmdMobWardSpawnScan {

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipEmptyWardScan(ServerLevel level, BlockPos.MutableBlockPos pos, CallbackInfoReturnable<Boolean> spawnResult, CallbackInfo ci) {
        if (!CoOConfig.bmdLeanWardSpawnScan) {
            return;
        }
        if (BmdBlockCacheState.skipScan(BmdBlockCacheState.MOB_WARD)) {
            ci.cancel();
        }
    }
}
