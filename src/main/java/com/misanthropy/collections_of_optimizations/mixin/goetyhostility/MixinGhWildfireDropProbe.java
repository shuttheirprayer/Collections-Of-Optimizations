package com.misanthropy.collections_of_optimizations.mixin.goetyhostility;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.ratrod.goetyhostility.common.entities.hostile.HostileWildfire", remap = false)
public abstract class MixinGhWildfireDropProbe {

    @Inject(method = "steepDropBelow", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$leanDropProbe(CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.goetyhostilityLeanWildfireDropProbe) {
            return;
        }
        if (!((Object) this instanceof Entity entity)) {
            return;
        }
        Level level = entity.level();
        BlockPos origin = entity.blockPosition();
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 8; i++) {
            probe.set(x, y - i, z);
            if (!level.getBlockState(probe).isAir()) {
                cir.setReturnValue(Boolean.FALSE);
                return;
            }
        }
        cir.setReturnValue(Boolean.TRUE);
    }
}
