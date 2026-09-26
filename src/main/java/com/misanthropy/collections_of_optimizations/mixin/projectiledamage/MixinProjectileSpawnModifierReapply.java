package com.misanthropy.collections_of_optimizations.mixin.projectiledamage;

import com.bawnorton.mixinsquared.TargetHandler;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = ServerLevel.class, priority = 1500)
public abstract class MixinProjectileSpawnModifierReapply {

    @TargetHandler(mixin = "net.projectile_damage.mixin.ServerWorldMixin", name = "pre_spawnEntity")
    @WrapWithCondition(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;addTransientAttributeModifiers(Lcom/google/common/collect/Multimap;)V"
            ),
            require = 0
    )
    private boolean coo$skipAlreadyAppliedWeaponModifiers(AttributeMap map, Multimap<Attribute, AttributeModifier> modifiers) {
        if (!CoOConfig.projectiledamageSkipAppliedWeaponModifiers) {
            return true;
        }
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            AttributeInstance instance = map.getInstance(entry.getKey());
            if (instance == null) {
                continue;
            }
            AttributeModifier wanted = entry.getValue();
            AttributeModifier current = instance.getModifier(wanted.getId());
            if (current == null || current.getAmount() != wanted.getAmount() || current.getOperation() != wanted.getOperation()) {
                return true;
            }
        }
        return false;
    }
}
