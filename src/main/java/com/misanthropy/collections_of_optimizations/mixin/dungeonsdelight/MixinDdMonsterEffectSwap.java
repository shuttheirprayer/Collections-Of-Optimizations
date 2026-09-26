package com.misanthropy.collections_of_optimizations.mixin.dungeonsdelight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.yirmiri.dungeonsdelight.common.effect.MonsterEffect", remap = false)
public abstract class MixinDdMonsterEffectSwap {

    @Shadow
    @Final
    private MobEffect normalVariant;

    @Inject(method = "m_6742_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$leanSwap(LivingEntity living, int amplifier, CallbackInfo ci) {
        if (!CoOConfig.dungeonsdelightLeanMonsterEffectSwap) {
            return;
        }
        ci.cancel();
        MobEffect normal = this.normalVariant;
        if (normal == null) {
            return;
        }
        MobEffectInstance old = living.getEffect(normal);
        if (old == null) {
            return;
        }
        int duration = old.getDuration();
        living.removeEffect(normal);
        living.addEffect(new MobEffectInstance((MobEffect) (Object) this, duration, 0));
    }
}
