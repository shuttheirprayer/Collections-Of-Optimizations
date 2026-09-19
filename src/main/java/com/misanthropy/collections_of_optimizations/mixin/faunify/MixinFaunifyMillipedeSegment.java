package com.misanthropy.collections_of_optimizations.mixin.faunify;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.pepper.faunify.entity.MillipedeSegmentEntity", remap = false)
public abstract class MixinFaunifyMillipedeSegment {

    @Unique
    private int coo$nextParentScanTick;

    @Inject(
            method = "getParentSegment()Lnet/minecraft/world/entity/Entity;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$throttleParentScan(CallbackInfoReturnable<Entity> cir) {
        int interval = CoOConfig.faunifyMillipedeParentScanInterval;
        if (interval <= 1) {
            return;
        }
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (level == null || !level.isClientSide()) {
            return;
        }
        if (self.tickCount < this.coo$nextParentScanTick) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            method = "getParentSegment()Lnet/minecraft/world/entity/Entity;",
            at = @At("RETURN"),
            require = 0
    )
    private void coo$recordParentScan(CallbackInfoReturnable<Entity> cir) {
        int interval = CoOConfig.faunifyMillipedeParentScanInterval;
        if (interval <= 1) {
            return;
        }
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (level == null || !level.isClientSide()) {
            return;
        }
        if (cir.getReturnValue() == null) {
            this.coo$nextParentScanTick = self.tickCount + interval;
        } else {
            this.coo$nextParentScanTick = 0;
        }
    }
}
