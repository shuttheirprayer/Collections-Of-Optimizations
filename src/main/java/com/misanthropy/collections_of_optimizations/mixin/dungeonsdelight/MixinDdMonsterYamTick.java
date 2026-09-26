package com.misanthropy.collections_of_optimizations.mixin.dungeonsdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.yirmiri.dungeonsdelight.common.entity.monster_yam.MonsterYamEntity", remap = false)
public abstract class MixinDdMonsterYamTick {

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
            require = 0
    )
    private boolean coo$leanAura(LivingEntity target, MobEffectInstance effect, Operation<Boolean> original) {
        if (CoOConfig.dungeonsdelightLeanYamAura && effect != null) {
            MobEffectInstance current = target.getEffect(effect.getEffect());
            if (current != null
                    && current.getAmplifier() == effect.getAmplifier()
                    && current.isAmbient() == effect.isAmbient()
                    && current.isVisible() == effect.isVisible()
                    && current.getDuration() > effect.getDuration() / 2) {
                return false;
            }
        }
        return original.call(target, effect);
    }

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"),
            require = 0
    )
    private boolean coo$noDoubleAdd(Level level, Entity entity, Operation<Boolean> original) {
        if (CoOConfig.dungeonsdelightNoDuplicateZombieAdd && entity != null && level instanceof ServerLevel server
                && server.getEntity(entity.getUUID()) != null) {
            return false;
        }
        return original.call(level, entity);
    }
}
