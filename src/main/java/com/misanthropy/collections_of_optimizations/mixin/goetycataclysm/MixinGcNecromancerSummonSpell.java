package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import com.Polarice3.goety_cataclysm.common.entities.neutral.AbstractDraugrNecromancer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mixin(value = AbstractDraugrNecromancer.SummonServantSpell.class, remap = false)
public abstract class MixinGcNecromancerSummonSpell {

    @Shadow
    @Final
    AbstractDraugrNecromancer this$0;

    @WrapOperation(
            method = "m_8036_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$skipCountWhenIdle(Level level, Class<Entity> type, AABB box, Predicate<? super Entity> filter,
                                               Operation<List<Entity>> original) {
        if (CoOConfig.goetycataclysmLeanNecromancerSummonCheck) {
            AbstractDraugrNecromancer necromancer = this.this$0;
            LivingEntity target = necromancer.getTarget();
            if (target == null || !target.isAlive() || necromancer.isSpellCasting()) {
                return Collections.emptyList();
            }
        }
        return original.call(level, type, box, filter);
    }
}
