package com.misanthropy.collections_of_optimizations.mixin.torchmaster;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.xalcon.torchmaster.common.logic.entityblocking.megatorch.MegatorchEntityBlockingLight", remap = false)
public abstract class MixinTMMegatorchLight {

    @Shadow
    public abstract boolean shouldBlockVillageSiege(BlockPos pos);

    @Inject(method = "shouldBlockEntity", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$rangeBeforeFilter(Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.torchmasterMegaTorchRangeCheckFirst || pos == null) {
            return;
        }
        if (!this.shouldBlockVillageSiege(pos)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
