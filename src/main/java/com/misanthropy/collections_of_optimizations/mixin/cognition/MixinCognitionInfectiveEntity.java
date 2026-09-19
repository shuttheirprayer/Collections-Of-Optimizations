package com.misanthropy.collections_of_optimizations.mixin.cognition;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.cyanogen.experienceobelisk.block_entities.bibliophage.AbstractInfectiveEntity", remap = false)
public abstract class MixinCognitionInfectiveEntity {

    @Inject(method = "infectAdjacent", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipClientInfection(Level level, BlockPos pos, CallbackInfo ci) {
        if (CoOConfig.cognitionSkipClientInfection && level != null && level.isClientSide) {
            ci.cancel();
        }
    }
}
