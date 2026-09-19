package com.misanthropy.collections_of_optimizations.mixin.adamsarsplus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.adamsmods.adamsarsplus.entities.effects.FlameDeityAuraEffect", remap = false)
public abstract class MixinAapFlameDeityAura {

    @Inject(
            method = "m_6742_(Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$throttleAuraScan(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (entity == null) {
            return;
        }
        Level level = entity.level();
        if (level == null) {
            return;
        }
        if (level.isClientSide()) {
            if (CoOConfig.adamsarsplusSkipClientFlameAura) {
                ci.cancel();
            }
            return;
        }
        int interval = CoOConfig.adamsarsplusFlameAuraScanInterval;
        if (interval > 1 && Math.floorMod(entity.tickCount, interval) != 0) {
            ci.cancel();
        }
    }
}
