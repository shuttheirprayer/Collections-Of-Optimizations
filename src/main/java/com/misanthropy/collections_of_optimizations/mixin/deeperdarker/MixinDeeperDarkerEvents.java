package com.misanthropy.collections_of_optimizations.mixin.deeperdarker;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DeeperDarkerRefs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kyanite.deeperdarker.DeeperDarkerEvents", remap = false)
public abstract class MixinDeeperDarkerEvents {

    @Inject(
            method = "breakEvent(Lnet/minecraftforge/event/level/BlockEvent$BreakEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipForeignBlockBreaks(BlockEvent.BreakEvent event, CallbackInfo ci) {
        if (!CoOConfig.deeperdarkerLeanBreakEvent) {
            return;
        }
        BlockState state = event.getState();
        if (state == null) {
            return;
        }
        if (!DeeperDarkerRefs.mayNeedBreakEvent(state.getBlock())) {
            ci.cancel();
        }
    }
}
