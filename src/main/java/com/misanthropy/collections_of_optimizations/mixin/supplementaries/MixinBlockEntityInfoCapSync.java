package com.misanthropy.collections_of_optimizations.mixin.supplementaries;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityInfo", priority = 1500)
public abstract class MixinBlockEntityInfoCapSync {

    @TargetHandler(
            mixin = "net.mehvahdjukaar.supplementaries.mixins.forge.ChunkHolderMixin",
            name = "sendBlockEntityCaps"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$skipNonSignCapSync(BlockEntity te, CallbackInfoReturnable<?> cir, CallbackInfo ci) {
        if (CoOConfig.supplementariesSkipNonSignCapSync && !(te instanceof SignBlockEntity)) {
            ci.cancel();
        }
    }
}
