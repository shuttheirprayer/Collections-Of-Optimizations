package com.misanthropy.collections_of_optimizations.mixin.legendaryspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LsTornadoState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.Tornado", remap = false)
public abstract class MixinLsTornadoTracker {

    @Inject(method = {"m_8119_", "tick"}, at = @At("HEAD"), require = 0)
    private void coo$markTornadoPresent(CallbackInfo ci) {
        if (!CoOConfig.legendaryspellbooksLeanTornadoScan) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (!self.level().isClientSide) {
            LsTornadoState.markTornadoTick(self);
        }
    }
}
