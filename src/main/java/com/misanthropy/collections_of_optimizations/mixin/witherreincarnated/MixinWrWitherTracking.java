package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.misanthropy.collections_of_optimizations.core.WrState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinWrWitherTracking {

    @Inject(method = "onAddedToWorld", at = @At("TAIL"), remap = false)
    private void coo$witherAdded(CallbackInfo ci) {
        if ((Object) this instanceof WitherBoss wither) {
            WrState.witherAdded(wither.level().isClientSide);
        }
    }

    @Inject(method = "onRemovedFromWorld", at = @At("TAIL"), remap = false)
    private void coo$witherRemoved(CallbackInfo ci) {
        if ((Object) this instanceof WitherBoss wither) {
            WrState.witherRemoved(wither.level().isClientSide);
        }
    }
}
