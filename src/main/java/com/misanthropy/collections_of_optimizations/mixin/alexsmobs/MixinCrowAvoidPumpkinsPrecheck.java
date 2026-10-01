package com.misanthropy.collections_of_optimizations.mixin.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SectionPrecheck;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityCrow$AIAvoidPumpkins", remap = false)
public abstract class MixinCrowAvoidPumpkinsPrecheck {

    @Shadow
    @Final
    private int searchLength;

    @Shadow
    @Final
    EntityCrow this$0;

    @Inject(method = "searchForDestination", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipEmptyPumpkinScan(CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.alexsmobsCrowScanPrecheck) {
            return;
        }
        BlockPos pos = this.this$0.blockPosition();
        int horizontal = this.searchLength - 1;
        if (!SectionPrecheck.mayContain(this.this$0.level(),
                pos.getX() - horizontal, pos.getY() - 9, pos.getZ() - horizontal,
                pos.getX() + horizontal, pos.getY() + 1, pos.getZ() + horizontal,
                state -> state.is(AMTagRegistry.CROW_FEARS))) {
            cir.setReturnValue(false);
        }
    }
}
