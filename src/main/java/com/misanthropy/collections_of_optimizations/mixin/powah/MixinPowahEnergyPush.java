package com.misanthropy.collections_of_optimizations.mixin.powah;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "owmii.powah.forge.ForgeEnvHandler", remap = false)
public abstract class MixinPowahEnergyPush {

    @Inject(
            method = "pushEnergy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;J)J",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$skipIdleEnergyPush(Level level, BlockPos pos, Direction side, long howMuch, CallbackInfoReturnable<Long> cir) {
        if (!CoOConfig.powahSkipIdleEnergyPush) {
            return;
        }
        if (howMuch <= 0L) {
            cir.setReturnValue(0L);
        }
    }
}
